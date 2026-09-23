package com.example.myapplication.domain.ai

import com.example.myapplication.data.repository.SavedGame
import com.example.myapplication.domain.engine.MoveGenerator
import com.example.myapplication.domain.model.Color
import com.example.myapplication.domain.model.GameState
import com.example.myapplication.domain.model.Move
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds


interface ChessEngine {
    suspend fun findBestMove(state: GameState, difficulty: Difficulty): Move
    fun close()
}

enum class Difficulty { BEGINNER, EASY, MEDIUM, HARD, EXPERT }

sealed interface GameMode {
    object LocalTwoPlayer : GameMode
    data class VsAi(val aiColor: Color, val difficulty: Difficulty) : GameMode
}

sealed interface GameSource {
    data class NewGame(val gameMode: GameMode) : GameSource
    data class Resume(val savedGame: SavedGame) : GameSource
}