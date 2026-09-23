package com.example.myapplication.data.repository

import com.example.myapplication.data.SavedGameSummary
import com.example.myapplication.data.tables.SavedGameEntity
import com.example.myapplication.domain.ai.Difficulty
import com.example.myapplication.domain.ai.GameMode
import com.example.myapplication.domain.model.Color
import com.example.myapplication.domain.model.GameResult
import com.example.myapplication.domain.model.GameState
import com.example.myapplication.domain.model.Move
import kotlinx.coroutines.flow.Flow

interface SavedGamesRepository {
    suspend fun save(gameId: Long?, history: List<Pair<GameState, Move?>>, gameMode: GameMode, gameResult : GameResult?): Long
    suspend fun load(id: Long): SavedGame?
    suspend fun delete(id: Long)
    fun observeSummaries(): Flow<List<SavedGameSummary>>
}

data class SavedGame(
    val id: Long,
    val history: List<Pair<GameState, Move?>>,
    val gameMode: GameMode
)

fun GameMode.toEntityFields(): Triple<String, String?, String?> = when (this) {
    is GameMode.LocalTwoPlayer -> Triple("LOCAL", null, null)
    is GameMode.VsAi -> Triple("AI", aiColor.name, difficulty.name)
}

fun SavedGameEntity.toGameMode(): GameMode = when (opponentType) {
    "LOCAL" -> GameMode.LocalTwoPlayer
    "AI" -> GameMode.VsAi(
        aiColor = Color.valueOf(aiColor!!),
        difficulty = Difficulty.valueOf(difficulty!!)
    )
    else -> error("Unknown opponentType in saved game: $opponentType")
}