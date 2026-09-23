package com.example.myapplication.domain.model

sealed interface GameResult {
    data class Checkmate(val winner: Color) : GameResult
    object Stalemate : GameResult
    object FiftyMoveDraw : GameResult
    object ThreefoldRepetition : GameResult
    object InsufficientMaterial : GameResult
}