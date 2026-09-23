package com.example.myapplication.domain.model

import com.example.myapplication.domain.engine.Zobrist

data class GameState(
    val board: Board,
    val sideToMove: Color,
    val castlingRights: CastlingRights,
    val enPassantTarget: Square?,
    val halfmoveClock: Int,
    val fullmoveNumber: Int,
    val history: List<Long>
) {
    val zobristHash: Long
        get() {
            var hash = board.pieceHash
            if (sideToMove == Color.BLACK) hash = hash xor Zobrist.blackToMove
            if (castlingRights.whiteKingside) hash = hash xor Zobrist.whiteKingside
            if (castlingRights.whiteQueenside) hash = hash xor Zobrist.whiteQueenside
            if (castlingRights.blackKingside) hash = hash xor Zobrist.blackKingside
            if (castlingRights.blackQueenside) hash = hash xor Zobrist.blackQueenside
            enPassantTarget?.let { hash = hash xor Zobrist.enPassantFile[Squares.file(it)] }
            return hash
        }
    fun applyMove(move: Move): GameState {
        val newBoard = board.withMove(move)
        val newCastling = castlingRights.updatedFor(move)
        val newEnPassant = if (move.isDoublePawnPush) {
            Squares.of(Squares.file(move.from), (Squares.rank(move.from) + Squares.rank(move.to)) / 2)
        } else null
        val newSideToMove = sideToMove.opposite()

        val provisional = GameState(
            board = newBoard,
            sideToMove = newSideToMove,
            castlingRights = newCastling,
            enPassantTarget = newEnPassant,
            halfmoveClock = if (move.capturedPiece != null || move.piece.type == PieceType.PAWN) 0 else halfmoveClock + 1,
            fullmoveNumber = if (sideToMove == Color.BLACK) fullmoveNumber + 1 else fullmoveNumber,
            history = history // patched below
        )
        return provisional.copy(history = history + provisional.zobristHash)
    }

    fun isInCheck(color: Color = sideToMove): Boolean =
        board.isSquareAttacked(board.kingSquare(color), by = color.opposite())

    fun isThreefoldRepetition(): Boolean =
        history.groupingBy { it }.eachCount().values.any { it >= 3 }

    fun isFiftyMoveDraw(): Boolean = halfmoveClock >= 100

    fun hasInsufficientMaterial(): Boolean{
        var numOfWhiteKnights = 0
        var numOfBlackKnights = 0
        var numOfWhiteBishops = 0
        var numOfBlackBishops = 0
        for(i in 0..127){
            if(!Squares.isOnBoard(i)) continue
            if(board[i]?.type == PieceType.PAWN) return false
            if(board[i]?.type == PieceType.KNIGHT && board[i]?.color == Color.WHITE) numOfWhiteKnights += 1
            if(board[i]?.type == PieceType.KNIGHT && board[i]?.color == Color.BLACK) numOfBlackKnights += 1
            if(board[i]?.type == PieceType.BISHOP && board[i]?.color == Color.WHITE) numOfWhiteBishops += 1
            if(board[i]?.type == PieceType.BISHOP && board[i]?.color == Color.BLACK) numOfBlackBishops += 1
            if(board[i]?.type == PieceType.ROOK) return false
            if(board[i]?.type == PieceType.QUEEN) return false
        }
        if(numOfWhiteBishops == 2) return false
        if(numOfBlackBishops == 2) return false
        if(numOfWhiteBishops == 1 && numOfWhiteKnights >= 1) return false
        if(numOfBlackBishops == 1 && numOfBlackKnights >= 1) return false
        return true
    }

    companion object {
        fun newGame(): GameState = GameState(
            board = Board.startingPosition(),
            sideToMove = Color.WHITE,
            castlingRights = CastlingRights(),
            enPassantTarget = null,
            halfmoveClock = 0,
            fullmoveNumber = 1,
            history = emptyList()
        )
    }
}

fun Color.opposite() = if (this == Color.WHITE) Color.BLACK else Color.WHITE

private val A1 = Squares.of(0, 0)
private val H1 = Squares.of(7, 0)
private val A8 = Squares.of(0, 7)
private val H8 = Squares.of(7, 7)

private fun CastlingRights.updatedFor(move: Move): CastlingRights {
    var rights = this

    // King move (normal or castle) revokes both rights for that color
    if (move.piece.type == PieceType.KING) {
        rights = when (move.piece.color) {
            Color.WHITE -> rights.copy(whiteKingside = false, whiteQueenside = false)
            Color.BLACK -> rights.copy(blackKingside = false, blackQueenside = false)
        }
    }

    // Rook moving away from its home square revokes that one right
    if (move.piece.type == PieceType.ROOK) {
        when (move.from) {
            A1 -> rights = rights.copy(whiteQueenside = false)
            H1 -> rights = rights.copy(whiteKingside = false)
            A8 -> rights = rights.copy(blackQueenside = false)
            H8 -> rights = rights.copy(blackKingside = false)
        }
    }

    // A rook captured on its home square revokes that right too,
    // regardless of which side made the capture
    if (move.capturedPiece?.type == PieceType.ROOK) {
        when (move.to) {
            A1 -> if (move.capturedPiece.color == Color.WHITE) rights = rights.copy(whiteQueenside = false)
            H1 -> if (move.capturedPiece.color == Color.WHITE) rights = rights.copy(whiteKingside = false)
            A8 -> if (move.capturedPiece.color == Color.BLACK) rights = rights.copy(blackQueenside = false)
            H8 -> if (move.capturedPiece.color == Color.BLACK) rights = rights.copy(blackKingside = false)
        }
    }

    return rights
}