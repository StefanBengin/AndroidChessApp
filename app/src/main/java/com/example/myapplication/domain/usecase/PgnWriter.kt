package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.engine.MoveGenerator
import com.example.myapplication.domain.model.CastleSide
import com.example.myapplication.domain.model.GameState
import com.example.myapplication.domain.model.Move
import com.example.myapplication.domain.model.PieceType
import com.example.myapplication.domain.model.Squares

object PgnWriter {
    fun write(moves: List<Move>): String =
        writeSanList(moves).chunked(2).mapIndexed { i, pair ->
            "${i + 1}. ${pair.joinToString(" ")}"
        }.joinToString(" ")

    fun writeSanList(moves: List<Move>): List<String> {
        var state = GameState.newGame()
        val result = mutableListOf<String>()
        for (move in moves) {
            result.add(sanFor(state, move))
            state = state.applyMove(move)
        }
        return result
    }

    private fun sanFor(state: GameState, move: Move): String {
        val base = when {
            move.castleSide == CastleSide.KINGSIDE -> "O-O"
            move.castleSide == CastleSide.QUEENSIDE -> "O-O-O"
            else -> nonCastleSan(state, move)
        }
        val next = state.applyMove(move)
        val suffix = checkOrMateSuffix(next)
        return base + suffix
    }

    private fun nonCastleSan(state: GameState, move: Move): String {
        val sb = StringBuilder()
        val isCapture = move.capturedPiece != null || move.isEnPassant

        if (move.piece.type == PieceType.PAWN) {
            if (isCapture) sb.append(Squares.fileChar(move.from))
        } else {
            sb.append(move.piece.type.symbol)
            sb.append(disambiguator(state, move))
        }

        if (isCapture) sb.append('x')
        sb.append(Squares.toAlgebraic(move.to))

        move.promotion?.let { sb.append('=').append(it.symbol) }

        return sb.toString()
    }

    /**
     * Returns "", a file letter, a rank digit, or both — whichever is the minimal
     * disambiguation needed among other legal moves of the same piece type to the same square.
     */
    private fun disambiguator(state: GameState, move: Move): String {
        val sameTypeSameDestination = MoveGenerator.legalMoves(state).filter {
            it.piece.type == move.piece.type &&
                    it.piece.color == move.piece.color &&
                    it.to == move.to &&
                    it.from != move.from
        }
        if (sameTypeSameDestination.isEmpty()) return ""

        val sameFile = sameTypeSameDestination.any { Squares.file(it.from) == Squares.file(move.from) }
        val sameRank = sameTypeSameDestination.any { Squares.rank(it.from) == Squares.rank(move.from) }

        return when {
            !sameFile -> Squares.fileChar(move.from).toString()          // file alone disambiguates
            sameFile && !sameRank -> Squares.rankChar(move.from).toString() // rank alone disambiguates
            else -> Squares.toAlgebraic(move.from)                        // need both (rare — e.g. 3 queens)
        }
    }

    private fun checkOrMateSuffix(state: GameState): String {
        val inCheck = state.isInCheck()
        if (!inCheck) return ""
        val hasLegalMove = MoveGenerator.legalMoves(state).isNotEmpty()
        return if (hasLegalMove) "+" else "#"
    }
}