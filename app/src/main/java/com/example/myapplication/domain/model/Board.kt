package com.example.myapplication.domain.model

import com.example.myapplication.domain.engine.Directions
import com.example.myapplication.domain.engine.Zobrist

class Board private constructor(
    private val squares: Array<Piece?>,
    val whiteKingSquare: Square,
    val blackKingSquare: Square,
    val pieceHash: Long
) {
    operator fun get(sq: Square): Piece? {
        require(Squares.isOnBoard(sq))
        return squares[sq]
    }

    fun kingSquare(color: Color): Square =
        if (color == Color.WHITE) whiteKingSquare else blackKingSquare

    fun withMove(move: Move): Board {
        val next = squares.copyOf()
        var hash = pieceHash

        val placedPiece = move.promotion?.let { Piece(it, move.piece.color) } ?: move.piece

        hash = hash xor Zobrist.pieceSquare(move.piece, move.from)
        next[move.from] = null

        if (move.isEnPassant) {
            val capturedSq = Squares.of(Squares.file(move.to), Squares.rank(move.from))
            val capturedPawn = next[capturedSq]
            if (capturedPawn != null) hash = hash xor Zobrist.pieceSquare(capturedPawn, capturedSq)
            next[capturedSq] = null
        } else if (move.capturedPiece != null) {
            hash = hash xor Zobrist.pieceSquare(move.capturedPiece, move.to)
        }

        next[move.to] = placedPiece
        hash = hash xor Zobrist.pieceSquare(placedPiece, move.to)

        if (move.castleSide != null) {
            val rank = Squares.rank(move.from)
            val (rookFrom, rookTo) = when (move.castleSide) {
                CastleSide.KINGSIDE -> Squares.of(7, rank) to Squares.of(5, rank)
                CastleSide.QUEENSIDE -> Squares.of(0, rank) to Squares.of(3, rank)
            }
            val rook = next[rookFrom]!!
            hash = hash xor Zobrist.pieceSquare(rook, rookFrom)
            next[rookFrom] = null
            next[rookTo] = rook
            hash = hash xor Zobrist.pieceSquare(rook, rookTo)
        }

        val newWhiteKing = if (move.piece.type == PieceType.KING && move.piece.color == Color.WHITE)
            move.to else whiteKingSquare
        val newBlackKing = if (move.piece.type == PieceType.KING && move.piece.color == Color.BLACK)
            move.to else blackKingSquare

        return Board(next, newWhiteKing, newBlackKing, hash)
    }

    fun isSquareAttacked(sq: Square, by: Color): Boolean {
        val pawnAttackerOffsets = if (by == Color.WHITE) PAWN_ATTACK_FROM_WHITE else PAWN_ATTACK_FROM_BLACK
        for (offset in pawnAttackerOffsets) {
            val from = sq + offset
            if (Squares.isOnBoard(from)) {
                val piece = squares[from]
                if (piece != null && piece.color == by && piece.type == PieceType.PAWN) return true
            }
        }

        for (offset in Directions.KNIGHT_OFFSETS) {
            val from = sq + offset
            if (Squares.isOnBoard(from)) {
                val piece = squares[from]
                if (piece != null && piece.color == by && piece.type == PieceType.KNIGHT) return true
            }
        }

        for (offset in Directions.KING_OFFSETS) {
            val from = sq + offset
            if (Squares.isOnBoard(from)) {
                val piece = squares[from]
                if (piece != null && piece.color == by && piece.type == PieceType.KING) return true
            }
        }

        for (dir in Directions.BISHOP_DIRECTIONS) {
            var from = sq + dir
            while (Squares.isOnBoard(from)) {
                val piece = squares[from]
                if (piece != null) {
                    if (piece.color == by && (piece.type == PieceType.BISHOP || piece.type == PieceType.QUEEN)) return true
                    break // any piece blocks the ray, friend or foe
                }
                from += dir
            }
        }

        for (dir in Directions.ROOK_DIRECTIONS) {
            var from = sq + dir
            while (Squares.isOnBoard(from)) {
                val piece = squares[from]
                if (piece != null) {
                    if (piece.color == by && (piece.type == PieceType.ROOK || piece.type == PieceType.QUEEN)) return true
                    break
                }
                from += dir
            }
        }

        return false
    }

    companion object {
        private val PAWN_ATTACK_FROM_WHITE = intArrayOf(-15, -17) // white attacker sits below-left/below-right of sq
        private val PAWN_ATTACK_FROM_BLACK = intArrayOf(15, 17)   // black attacker sits above-left/above-right of sq
        fun startingPosition(): Board {
            val squares = arrayOfNulls<Piece?>(128)
            val backRank = listOf(
                PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
                PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
            )
            for (file in 0..7) {
                squares[Squares.of(file, 0)] = Piece(backRank[file], Color.WHITE)
                squares[Squares.of(file, 1)] = Piece(PieceType.PAWN, Color.WHITE)
                squares[Squares.of(file, 6)] = Piece(PieceType.PAWN, Color.BLACK)
                squares[Squares.of(file, 7)] = Piece(backRank[file], Color.BLACK)
            }
            return Board(squares, Squares.of(4, 0), Squares.of(4, 7), Zobrist.pieceHashOf(squares))
        }

        fun empty(): Board = Board(arrayOfNulls(128), -1, -1, 0L)
    }
}