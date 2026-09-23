package com.example.myapplication.domain.engine

import com.example.myapplication.domain.model.GameResult
import com.example.myapplication.domain.model.GameState
import com.example.myapplication.domain.model.opposite

object GameResultDetector {
    fun detect(state: GameState): GameResult? {
        val moves = MoveGenerator.legalMoves(state)
        return when {
            moves.isEmpty() && state.isInCheck() -> GameResult.Checkmate(winner = state.sideToMove.opposite())
            moves.isEmpty() -> GameResult.Stalemate
            state.isFiftyMoveDraw() -> GameResult.FiftyMoveDraw
            state.isThreefoldRepetition() -> GameResult.ThreefoldRepetition
            state.hasInsufficientMaterial() -> GameResult.InsufficientMaterial
            else -> null
        }
    }
}