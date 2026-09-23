package com.example.myapplication.ui.savedgames

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.myapplication.data.local.BoardTheme
import com.example.myapplication.data.local.PieceSet
import com.example.myapplication.domain.model.Color
import com.example.myapplication.domain.model.Piece
import com.example.myapplication.domain.model.PieceType
import com.example.myapplication.domain.model.Square
import com.example.myapplication.domain.model.Squares
import com.example.myapplication.ui.common.PieceImage
import com.example.myapplication.ui.theme.BoardColors
import kotlin.text.iterator

@Composable
fun BoardThumbnail(fen: String, boardTheme: BoardTheme, pieceSet : PieceSet, modifier: Modifier = Modifier) {
    val piecesBySquare = remember(fen) { parsePiecePlacement(fen) }

    Column(modifier.aspectRatio(1f)) {
        for (rank in 7 downTo 0) {
            Row(Modifier.weight(1f)) {
                for (file in 0..7) {
                    val square = Squares.of(file, rank)
                    val isLight = (file + rank) % 2 == 1
                    Box(
                        Modifier.weight(1f).fillMaxHeight()
                            .background(
                                if (isLight) BoardColors.forTheme(boardTheme).lightSquare
                                else BoardColors.forTheme(boardTheme).darkSquare
                            )
                    ) {
                        piecesBySquare[square]?.let { PieceImage(it, pieceSet = pieceSet, Modifier.fillMaxSize()) }
                    }
                }
            }
        }
    }
}

private fun parsePiecePlacement(fen: String): Map<Square, Piece> {
    val result = mutableMapOf<Square, Piece>()
    var rank = 7
    var file = 0
    for (c in fen) {
        when {
            c == '/' -> { rank--; file = 0 }
            c.isDigit() -> file += c.digitToInt()
            else -> {
                val color = if (c.isUpperCase()) Color.WHITE else Color.BLACK
                val type = PieceType.entries.first { it.symbol == c.uppercaseChar() }
                result[Squares.of(file, rank)] = Piece(type, color)
                file++
            }
        }
    }
    return result
}