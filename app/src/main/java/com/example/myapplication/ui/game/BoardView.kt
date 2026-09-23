package com.example.myapplication.ui.game

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.myapplication.data.local.BoardTheme
import com.example.myapplication.data.local.PieceSet
import com.example.myapplication.domain.model.*
import com.example.myapplication.ui.theme.BoardColors
import com.example.myapplication.domain.model.Color as ChessColor

@Composable
fun BoardView(
    positionedPieces : List<PositionedPiece>,
    selectedSquare: Square?,
    legalDestinations: Set<Square>,
    lastMove: Move?,
    checkedKingSquare: Square?,
    theme: BoardTheme,
    pieceSet: PieceSet,
    showCoordinates: Boolean,
    orientation: ChessColor = ChessColor.WHITE,
    onSquareTapped: (Square) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = remember(theme) { BoardColors.forTheme(theme) }
    val piecesBySquare = remember(positionedPieces) { positionedPieces.associate { it.square to it.piece } }

    BoxWithConstraints(modifier = modifier.aspectRatio(1f)) {
        val squareSizeDp = maxWidth / 8

        Box {
            // background layer: squares + highlights only, no piece drawing anymore
            Column {
                for (displayRow in 0..7) {
                    Row {
                        for (displayCol in 0..7) {
                            val square = displayToSquare(displayRow, displayCol, orientation)
                            val isLight = (Squares.file(square) + Squares.rank(square)) % 2 == 1
                            SquareBackground(
                                size = squareSizeDp,
                                colors = colors,
                                isLight = isLight,
                                isSelected = square == selectedSquare,
                                isLegalDestination = square in legalDestinations,
                                hasLegalCapture = square in legalDestinations && piecesBySquare[square] != null,
                                isLastMoveEndpoint = square == lastMove?.from || square == lastMove?.to,
                                isCheckedKing = square == checkedKingSquare,
                                coordinateLabel = if (showCoordinates) coordinateLabelFor(square, displayRow, displayCol) else null,
                                onTap = { onSquareTapped(square) }
                            )
                        }
                    }
                }
            }

            // foreground layer: animated pieces, floating on top, ignores its own tap events
            PieceLayer(positionedPieces, squareSizeDp, orientation, pieceSet)
        }
    }
}

// Converts a (displayRow, displayCol) grid position — row 0 = top of screen —
// into the actual 0x88 board square, accounting for which side is at the bottom.
private fun displayToSquare(displayRow: Int, displayCol: Int, orientation: ChessColor): Square {
    val rank = if (orientation == ChessColor.WHITE) 7 - displayRow else displayRow
    val file = if (orientation == ChessColor.WHITE) displayCol else 7 - displayCol
    return Squares.of(file, rank)
}

/**
 * Standard chess board convention: file letters (a-h) along the bottom edge only,
 * rank digits (1-8) along the left edge only — not repeated on every square.
 */
private fun coordinateLabelFor(square: Square, displayRow: Int, displayCol: Int): String? {
    val isBottomRow = displayRow == 7
    val isLeftCol = displayCol == 0
    return when {
        isBottomRow && isLeftCol -> "${Squares.fileChar(square)}${Squares.rankChar(square)}"
        isBottomRow -> Squares.fileChar(square).toString()
        isLeftCol -> Squares.rankChar(square).toString()
        else -> null
    }
}