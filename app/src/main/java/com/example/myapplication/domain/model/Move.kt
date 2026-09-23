package com.example.myapplication.domain.model

enum class CastleSide { KINGSIDE, QUEENSIDE }

data class Move(
    val from: Square,
    val to: Square,
    val piece: Piece,
    val capturedPiece: Piece? = null,
    val promotion: PieceType? = null,
    val isEnPassant: Boolean = false,
    val castleSide: CastleSide? = null,
    val isDoublePawnPush: Boolean = false
)