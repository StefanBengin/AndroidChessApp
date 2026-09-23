package com.example.myapplication.domain.model

enum class Color { WHITE, BLACK }

enum class PieceType(val symbol: Char, val pointValue: Int) {
    PAWN('P', 1), KNIGHT('N', 3), BISHOP('B', 3), ROOK('R', 5), QUEEN('Q', 9), KING('K', 0)
}

data class Piece(val type: PieceType, val color: Color)