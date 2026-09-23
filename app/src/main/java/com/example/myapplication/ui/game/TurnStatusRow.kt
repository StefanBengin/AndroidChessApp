package com.example.myapplication.ui.game

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.ai.GameMode
import com.example.myapplication.domain.model.Color

@Composable
fun TurnStatusRow(sideToMove: Color, isAiThinking: Boolean, gameMode: GameMode) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val label = when {
            isAiThinking -> "Computer is thinking…"
            gameMode is GameMode.VsAi && gameMode.aiColor == sideToMove -> "Computer's turn"
            else -> if (sideToMove == Color.WHITE) "White to move" else "Black to move"
        }
        if (isAiThinking) {
            CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
            Spacer(Modifier.width(8.dp))
        }
        Text(label, style = MaterialTheme.typography.bodyMedium)
    }
}