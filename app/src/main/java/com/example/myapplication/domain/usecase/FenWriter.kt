package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.Board
import com.example.myapplication.domain.model.Color
import com.example.myapplication.domain.model.GameState
import com.example.myapplication.domain.model.Squares

object FenWriter {
    fun writePiecePlacement(board: Board): String {
        val sb = StringBuilder()
        for (rank in 7 downTo 0) {
            var emptyCount = 0
            for (file in 0..7) {
                val piece = board[Squares.of(file, rank)]
                if (piece == null) {
                    emptyCount++
                } else {
                    if (emptyCount > 0) { sb.append(emptyCount); emptyCount = 0 }
                    val ch = piece.type.symbol
                    sb.append(if (piece.color == Color.WHITE) ch else ch.lowercaseChar())
                }
            }
            if (emptyCount > 0) sb.append(emptyCount)
            if (rank > 0) sb.append('/')
        }
        return sb.toString()
    }
    fun write(state: GameState): String {
        val placement = writePiecePlacement(state.board)
        val side = if (state.sideToMove == Color.WHITE) "w" else "b"
        val castling = buildString {
            if (state.castlingRights.whiteKingside) append('K')
            if (state.castlingRights.whiteQueenside) append('Q')
            if (state.castlingRights.blackKingside) append('k')
            if (state.castlingRights.blackQueenside) append('q')
        }.ifEmpty { "-" }
        val enPassant = state.enPassantTarget?.let { Squares.toAlgebraic(it) } ?: "-"

        return "$placement $side $castling $enPassant ${state.halfmoveClock} ${state.fullmoveNumber}"
    }
}