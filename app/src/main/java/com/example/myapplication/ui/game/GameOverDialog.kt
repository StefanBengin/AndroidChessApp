package com.example.myapplication.ui.game

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.myapplication.domain.model.GameResult

@Composable
fun GameOverDialog(result: GameResult, onRematch: () -> Unit, onExit: () -> Unit) {
    AlertDialog(
        onDismissRequest = onExit,
        title = { Text(gameOverTitle(result)) },
        confirmButton = { TextButton(onClick = onRematch) { Text("Rematch") } },
        dismissButton = { TextButton(onClick = onExit) { Text("Main Menu") } }
    )
}

private fun gameOverTitle(result: GameResult): String = when (result) {
    is GameResult.Checkmate -> "Checkmate — ${result.winner} wins"
    GameResult.Stalemate -> "Draw by stalemate"
    GameResult.FiftyMoveDraw -> "Draw — 50-move rule"
    GameResult.ThreefoldRepetition -> "Draw — threefold repetition"
    GameResult.InsufficientMaterial -> "Draw — insufficient material"
}