package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.engine.MoveGenerator
import com.example.myapplication.domain.model.CastleSide
import com.example.myapplication.domain.model.GameState
import com.example.myapplication.domain.model.Move
import com.example.myapplication.domain.model.PieceType
import com.example.myapplication.domain.model.Square
import com.example.myapplication.domain.model.Squares

object PgnParser {

    /** Parses a PGN movetext string into a list of resolved Moves, replaying through GameState as it goes. */
    fun parseMoveList(pgn: String): List<Move> {
        val tokens = tokenize(pgn)
        var state = GameState.newGame()
        val moves = mutableListOf<Move>()

        for (token in tokens) {
            val move = resolveSanToken(state, token)
                ?: error("Could not resolve SAN token '$token' at move ${moves.size + 1} — " +
                        "either malformed PGN or a move illegal in this position")
            moves.add(move)
            state = state.applyMove(move)
        }

        return moves
    }

    /** Strips move numbers ("12.", "12...") and result markers ("1-0", "1/2-1/2", "*"), splits on whitespace. */
    private fun tokenize(pgn: String): List<String> {
        return pgn
            .replace(Regex("""\d+\.(\.\.)?"""), "")   // "1." or "12..." move-number markers
            .replace(Regex("""(1-0|0-1|1/2-1/2|\*)$"""), "") // trailing result marker
            .trim()
            .split(Regex("""\s+"""))
            .filter { it.isNotBlank() }
    }

    private fun resolveSanToken(state: GameState, token: String): Move? {
        val clean = token.trimEnd('+', '#') // strip check/mate suffix — informational only, not needed to resolve
        val legalMoves = MoveGenerator.legalMoves(state)

        if (clean == "O-O") return legalMoves.find { it.castleSide == CastleSide.KINGSIDE }
        if (clean == "O-O-O") return legalMoves.find { it.castleSide == CastleSide.QUEENSIDE }

        val promotion = clean.substringAfter('=', "").firstOrNull()?.let { symbolToPieceType(it) }
        val withoutPromotion = clean.substringBefore('=')

        val isCapture = 'x' in withoutPromotion
        val destinationStr = withoutPromotion.takeLast(2)
        val destination = Squares.fromAlgebraic(destinationStr)

        val prefix = withoutPromotion.dropLast(2).removeSuffix("x")
        val pieceType = if (prefix.isNotEmpty() && prefix[0].isUpperCase() && prefix[0] != 'x')
            symbolToPieceType(prefix[0]) else PieceType.PAWN
        val disambiguator = if (pieceType == PieceType.PAWN) prefix else prefix.drop(1)

        val candidates = legalMoves.filter { move ->
            move.piece.type == pieceType &&
                    move.to == destination &&
                    move.promotion == promotion &&
                    (isCapture == (move.capturedPiece != null || move.isEnPassant)) &&
                    matchesDisambiguator(move.from, disambiguator)
        }

        return when (candidates.size) {
            1 -> candidates.first()
            0 -> null // no legal move matches — malformed PGN or illegal position
            else -> null // token was ambiguous even after filtering — malformed PGN (should never happen for valid SAN)
        }
    }

    private fun matchesDisambiguator(from: Square, disambiguator: String): Boolean {
        if (disambiguator.isEmpty()) return true
        return when (disambiguator.length) {
            1 -> {
                val c = disambiguator[0]
                if (c.isDigit()) Squares.rankChar(from) == c else Squares.fileChar(from) == c
            }
            2 -> Squares.toAlgebraic(from) == disambiguator
            else -> false
        }
    }

    fun parseHistory(pgn: String): List<Pair<GameState, Move?>> {
        val moves = parseMoveList(pgn)
        var state = GameState.newGame()
        val history = mutableListOf(state to (null as Move?))
        for (move in moves) {
            state = state.applyMove(move)
            history.add(state to move)
        }
        return history
    }

    private fun symbolToPieceType(c: Char): PieceType =
        PieceType.entries.first { it.symbol == c }
}