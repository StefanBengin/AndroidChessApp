package com.example.myapplication.ui.game

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.ai.GameMode
import com.example.myapplication.domain.model.Color
import com.example.myapplication.ui.settings.SettingsViewModel
import com.example.myapplication.domain.model.Color as ChessColor
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.launch

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    settingsViewModel: SettingsViewModel,
    onExitGame: () -> Unit,
    onRematch: (GameMode) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val settings by settingsViewModel.settings.collectAsState()
    var exitConfirmVisible by remember { mutableStateOf(false) }

    var isFlipped by rememberSaveable { mutableStateOf(false) }
    val orientation = if (isFlipped) ChessColor.BLACK else ChessColor.WHITE

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val feedbackPlayer = remember { GameFeedbackPlayer(context) }
    DisposableEffect(Unit) { onDispose { feedbackPlayer.release() } }

    LaunchedEffect(state.lastMove) {
        val move = state.lastMove ?: return@LaunchedEffect
        feedbackPlayer.onMovePlayed(move, isCheck = state.checkedKingSquare != null, settings)
    }

    Scaffold(
        topBar = {
            GameTopBar(
                canUndo = state.canUndo,
                canRedo = state.canRedo,
                onUndo = viewModel::undo,
                onRedo = viewModel::redo,
                onExitRequested = { exitConfirmVisible = true }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            TurnStatusRow(state.sideToMove, state.isAiThinking, state.gameMode)

            val opponentCaptured = state.capturedPieces.filter { it.color != Color.WHITE }
            val playerCaptured = state.capturedPieces.filter { it.color != Color.BLACK }
            CapturedPiecesRow(opponentCaptured, settings.pieceSet)

            Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { isFlipped = !isFlipped }) {
                    Icon(Icons.Default.SwapVert, contentDescription = "Flip board")
                }
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            val pgn = viewModel.currentPgn()
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, pgn)
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share game"))
                        }
                    },
                    enabled = state.canUndo
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share PGN")
                }
            }

            BoardView(
                positionedPieces = state.positionedPieces,
                selectedSquare = state.selectedSquare,
                legalDestinations = if (settings.showLegalMoves) state.legalDestinations else emptySet(),
                lastMove = state.lastMove,
                checkedKingSquare = state.checkedKingSquare,
                theme = settings.boardTheme,
                pieceSet = settings.pieceSet,
                showCoordinates = settings.showCoordinates,
                orientation = orientation,
                onSquareTapped = viewModel::onSquareTapped,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            CapturedPiecesRow(playerCaptured, settings.pieceSet)
            MoveHistoryPanel(state.moveHistorySan)
        }
    }

    state.pendingPromotion?.let { pending ->
        PromotionPickerDialog(
            color = state.sideToMove,
            pieceSet = settings.pieceSet,
            onPieceChosen = viewModel::onPromotionChosen,
            onDismiss = viewModel::onPromotionCancelled
        )
    }

    state.gameResult?.let { result ->
        GameOverDialog(
            result = result,
            onExit = onExitGame,
            onRematch = { onRematch(state.gameMode) }
        )
    }

    if (exitConfirmVisible) {
        AlertDialog(
            onDismissRequest = { exitConfirmVisible = false },
            title = { Text("Leave game?") },
            text = { Text("Your progress is saved automatically — you can resume anytime from Saved Games.") },
            confirmButton = { TextButton(onClick = onExitGame) { Text("Leave") } },
            dismissButton = { TextButton(onClick = { exitConfirmVisible = false } ) { Text("Cancel") } }
        )
    }
}