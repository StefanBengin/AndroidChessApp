package com.example.myapplication.domain.engine

object Directions {
    val KNIGHT_OFFSETS = intArrayOf(33, 31, 18, 14, -33, -31, -18, -14)
    val KING_OFFSETS = intArrayOf(1, -1, 16, -16, 15, -15, 17, -17)
    val BISHOP_DIRECTIONS = intArrayOf(15, -15, 17, -17)
    val ROOK_DIRECTIONS = intArrayOf(1, -1, 16, -16)
}