package com.example.myapplication.ui.game

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.local.PieceSet
import com.example.myapplication.domain.model.Piece
import com.example.myapplication.domain.model.PieceType
import com.example.myapplication.ui.common.PieceImage


@Composable
fun CapturedPiecesRow(capturedByOpponent: List<Piece>, pieceSet: PieceSet) {
    // capturedByOpponent = pieces of ONE color that the OTHER side captured — rendered as small icons + point total
    Row(Modifier.padding(horizontal = 16.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        for (piece in capturedByOpponent.sortedByDescending { it.type.pointValue }) {
            PieceImage(piece, pieceSet, Modifier.size(18.dp))
        }
        val total = capturedByOpponent.sumOf { it.type.pointValue }
        if (total > 0) {
            Spacer(Modifier.width(4.dp))
            Text("+$total", style = MaterialTheme.typography.labelSmall)
        }
    }
}
