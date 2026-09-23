package com.example.myapplication.ui.savedgames

import android.text.format.DateUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.SavedGameSummary
import com.example.myapplication.data.local.BoardTheme
import com.example.myapplication.data.local.PieceSet
import com.example.myapplication.ui.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedGamesScreen(
    savedGamesViewModel: SavedGamesViewModel,
    settingsViewModel: SettingsViewModel,
    onGameSelected: (Long) -> Unit,
    onBack: () -> Unit
) {
    val state by savedGamesViewModel.uiState.collectAsState()
    val settings by settingsViewModel.settings.collectAsState()
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Games") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.games.isEmpty() -> {
                    EmptySavedGames(modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    LazyColumn {
                        items(state.games, key = { it.id }) { saved ->
                            SavedGameRow(
                                saved = saved,
                                boardTheme = settings.boardTheme,
                                pieceSet = settings.pieceSet,
                                onClick = { onGameSelected(saved.id) },
                                onDeleteRequested = { pendingDeleteId = saved.id }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }

    pendingDeleteId?.let { id ->
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text("Delete this game?") },
            text = { Text("This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    savedGamesViewModel.deleteGame(id)
                    pendingDeleteId = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteId = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun EmptySavedGames(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            Icons.Default.SportsEsports,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        Text("No saved games yet", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun SavedGameRow(saved: SavedGameSummary, boardTheme: BoardTheme, pieceSet: PieceSet, onClick: () -> Unit, onDeleteRequested: () -> Unit) {
    ListItem(
        leadingContent = { BoardThumbnail(saved.finalPositionFen, boardTheme, pieceSet, Modifier.size(48.dp)) },
        headlineContent = { Text("${saved.whiteLabel} vs ${saved.blackLabel}") },
        supportingContent = { Text("Move ${saved.moveNumber} · ${formatRelativeTime(saved.lastPlayedAt)}") },
        trailingContent = { IconButton(onClick = onDeleteRequested) { Icon(Icons.Default.Delete, null) } },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

private fun formatRelativeTime(epochMillis: Long): String {
    return DateUtils.getRelativeTimeSpanString(
        epochMillis, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS
    ).toString()
}