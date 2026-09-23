package com.example.myapplication.domain.engine
import com.example.myapplication.domain.model.*

object MoveGenerator {
    fun legalMoves(state: GameState): List<Move> {
        return pseudoLegalMoves(state).filter { move ->
            val next = state.board.withMove(move)
            !next.isSquareAttacked(next.kingSquare(state.sideToMove), by = state.sideToMove.opposite())
        }
    }

    fun pseudoLegalMoves(state: GameState): List<Move> {
        val moves = mutableListOf<Move>()
        val board = state.board
        val side = state.sideToMove

        for (sq in 0 until 128) {
            if (!Squares.isOnBoard(sq)) continue
            val piece = board[sq] ?: continue
            if (piece.color != side) continue

            when (piece.type) {
                PieceType.PAWN -> generatePawnMoves(state, sq, piece, moves)
                PieceType.KNIGHT -> generateOffsetMoves(board, sq, piece, Directions.KNIGHT_OFFSETS, moves)
                PieceType.KING -> {
                    generateOffsetMoves(board, sq, piece, Directions.KING_OFFSETS, moves)
                    generateCastlingMoves(state, sq, piece, moves)
                }
                PieceType.BISHOP -> generateSlidingMoves(board, sq, piece, Directions.BISHOP_DIRECTIONS, moves)
                PieceType.ROOK -> generateSlidingMoves(board, sq, piece, Directions.ROOK_DIRECTIONS, moves)
                PieceType.QUEEN -> {
                    generateSlidingMoves(board, sq, piece, Directions.BISHOP_DIRECTIONS, moves)
                    generateSlidingMoves(board, sq, piece, Directions.ROOK_DIRECTIONS, moves)
                }
            }
        }
        return moves
    }

    private fun generateOffsetMoves(
        board: Board, from: Square, piece: Piece, offsets: IntArray, out: MutableList<Move>
    ) {
        for (offset in offsets) {
            val to = from + offset
            if (!Squares.isOnBoard(to)) continue
            val target = board[to]
            if (target != null && target.color == piece.color) continue
            out.add(Move(from = from, to = to, piece = piece, capturedPiece = target))
        }
    }

    private fun generateSlidingMoves(
        board: Board, from: Square, piece: Piece, directions: IntArray, out: MutableList<Move>
    ) {
        for (dir in directions) {
            var to = from + dir
            while (Squares.isOnBoard(to)) {
                val target = board[to]
                if (target == null) {
                    out.add(Move(from = from, to = to, piece = piece))
                } else {
                    if (target.color != piece.color) {
                        out.add(Move(from = from, to = to, piece = piece, capturedPiece = target))
                    }
                    break
                }
                to += dir
            }
        }
    }

    private fun generatePawnMoves(state: GameState, from: Square, piece: Piece, out: MutableList<Move>) {
        val board = state.board
        val forward = if (piece.color == Color.WHITE) 16 else -16
        val startRank = if (piece.color == Color.WHITE) 1 else 6
        val promotionRank = if (piece.color == Color.WHITE) 7 else 0

        val onePush = from + forward
        if (Squares.isOnBoard(onePush) && board[onePush] == null) {
            addPawnMove(out, from, onePush, piece, null, promotionRank, isDoublePush = false)

            if (Squares.rank(from) == startRank) {
                val twoPush = from + forward * 2
                if (Squares.isOnBoard(twoPush) && board[twoPush] == null) {
                    out.add(Move(from = from, to = twoPush, piece = piece, isDoublePawnPush = true))
                }
            }
        }

        val captureOffsets = if (piece.color == Color.WHITE) intArrayOf(15, 17) else intArrayOf(-15, -17)
        for (offset in captureOffsets) {
            val to = from + offset
            if (!Squares.isOnBoard(to)) continue
            val target = board[to]
            if (target != null && target.color != piece.color) {
                addPawnMove(out, from, to, piece, target, promotionRank, isDoublePush = false)
            } else if (target == null && to == state.enPassantTarget) {
                out.add(Move(from = from, to = to, piece = piece, isEnPassant = true,
                    capturedPiece = Piece(PieceType.PAWN, piece.color.opposite())))
            }
        }
    }

    private fun addPawnMove(
        out: MutableList<Move>, from: Square, to: Square, piece: Piece,
        captured: Piece?, promotionRank: Int, isDoublePush: Boolean
    ) {
        if (Squares.rank(to) == promotionRank) {
            for (promo in listOf(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT)) {
                out.add(Move(from = from, to = to, piece = piece, capturedPiece = captured,
                    promotion = promo, isDoublePawnPush = isDoublePush))
            }
        } else {
            out.add(Move(from = from, to = to, piece = piece, capturedPiece = captured, isDoublePawnPush = isDoublePush))
        }
    }

    private fun generateCastlingMoves(state: GameState, kingFrom: Square, king: Piece, out: MutableList<Move>) {
        val board = state.board
        val color = king.color
        val rank = Squares.rank(kingFrom)
        val enemy = color.opposite()
        val rights = state.castlingRights

        val kingsideRight = if (color == Color.WHITE) rights.whiteKingside else rights.blackKingside
        val queensideRight = if (color == Color.WHITE) rights.whiteQueenside else rights.blackQueenside

        if (kingsideRight) {
            val f = Squares.of(5, rank)
            val g = Squares.of(6, rank)
            if (board[f] == null && board[g] == null &&
                !board.isSquareAttacked(kingFrom, enemy) &&
                !board.isSquareAttacked(f, enemy) &&
                !board.isSquareAttacked(g, enemy)) {
                out.add(Move(from = kingFrom, to = g, piece = king, castleSide = CastleSide.KINGSIDE))
            }
        }

        if (queensideRight) {
            val d = Squares.of(3, rank)
            val c = Squares.of(2, rank)
            val b = Squares.of(1, rank)
            if (board[d] == null && board[c] == null && board[b] == null &&
                !board.isSquareAttacked(kingFrom, enemy) &&
                !board.isSquareAttacked(d, enemy) &&
                !board.isSquareAttacked(c, enemy)) {
                out.add(Move(from = kingFrom, to = c, piece = king, castleSide = CastleSide.QUEENSIDE))
            }
        }
    }
}