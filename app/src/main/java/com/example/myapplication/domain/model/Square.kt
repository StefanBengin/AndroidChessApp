package com.example.myapplication.domain.model

// 0x88 square index: rank = sq shr 4, file = sq and 7, off-board if (sq and 0x88) != 0
typealias Square = Int

object Squares {
    fun of(file: Int, rank: Int): Square = (rank shl 4) or file
    fun file(sq: Square) = sq and 7 //sq % 8
    fun rank(sq: Square) = sq shr 4 //sq / 16
    fun isOnBoard(sq: Square) = (sq and 0x88) == 0
    fun toAlgebraic(sq: Square): String =
        "${'a' + file(sq)}${rank(sq) + 1}"
    fun fromAlgebraic(s: String): Square =
        of(s[0] - 'a', s[1] - '1')
    fun fileChar(sq: Square): Char = 'a' + Squares.file(sq)
    fun rankChar(sq: Square): Char = '1' + Squares.rank(sq)
}