package com.example.myapplication.domain.engine

import com.example.myapplication.domain.model.Color
import com.example.myapplication.domain.model.Piece
import com.example.myapplication.domain.model.PieceType
import com.example.myapplication.domain.model.Square
import kotlin.random.Random

object Zobrist {
    private val pieceTable: Map<Piece, Array<Long>> = buildMap {
        for (type in PieceType.entries) {
            for (color in Color.entries) {
                put(Piece(type, color), Array(128) { Random.nextLong() })
            }
        }
    }

    val blackToMove = Random.nextLong()
    val whiteKingside = Random.nextLong()
    val whiteQueenside = Random.nextLong()
    val blackKingside = Random.nextLong()
    val blackQueenside = Random.nextLong()
    val enPassantFile = Array(8) { Random.nextLong() }

    fun pieceSquare(piece: Piece, sq: Square): Long = pieceTable[piece]!![sq]

    fun pieceHashOf(squares: Array<Piece?>): Long {
        var hash = 0L
        for (i in 0..127) {
            val piece = squares[i] ?: continue
            hash = hash xor pieceSquare(piece, i)
        }
        return hash
    }
}