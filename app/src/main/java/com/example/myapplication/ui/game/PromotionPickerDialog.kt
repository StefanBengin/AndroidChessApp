package com.example.myapplication.ui.game

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.myapplication.data.local.PieceSet
import com.example.myapplication.domain.model.Color
import com.example.myapplication.domain.model.Piece
import com.example.myapplication.domain.model.PieceType

import com.example.myapplication.ui.common.PieceImage

@Composable
fun PromotionPickerDialog(
    color: Color,
    pieceSet: PieceSet,
    onPieceChosen: (PieceType) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card {
            Row(Modifier.padding(16.dp)) {
                for (type in listOf(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT)) {
                    PieceImage(
                        piece = Piece(type, color),
                        pieceSet = pieceSet,
                        modifier = Modifier.size(56.dp).clickable { onPieceChosen(type) }
                    )
                }
            }
        }
    }
}