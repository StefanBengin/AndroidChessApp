package com.example.myapplication.ui.game

import com.example.myapplication.domain.ai.GameMode
import com.example.myapplication.domain.model.*


data class GameUiState(
    val positionedPieces : List<PositionedPiece>,
    val sideToMove: Color,
    val selectedSquare: Square? = null,
    val legalDestinations: Set<Square> = emptySet(),   // for the currently selected piece only
    val lastMove: Move? = null,
    val checkedKingSquare: Square? = null,               // non-null only if that king is in check
    val pendingPromotion: PendingPromotion? = null,       // non-null while waiting for user to pick a piece
    val gameResult: GameResult? = null,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val isAiThinking: Boolean = false,
    val moveHistorySan: List<String> = emptyList(),  // one entry per ply, no move numbers — UI groups them
    val capturedPieces: List<Piece> = emptyList(),    // every piece captured so far, either color
    val gameMode: GameMode
)

data class PendingPromotion(val from: Square, val to: Square)