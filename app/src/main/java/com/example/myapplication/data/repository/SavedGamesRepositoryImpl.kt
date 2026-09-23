package com.example.myapplication.data.repository

import com.example.myapplication.data.dao.SavedGameDao
import com.example.myapplication.data.SavedGameSummary
import com.example.myapplication.data.tables.SavedGameEntity
import com.example.myapplication.domain.ai.GameMode
import com.example.myapplication.domain.engine.MoveGenerator
import com.example.myapplication.domain.model.Color
import com.example.myapplication.domain.model.GameResult
import com.example.myapplication.domain.usecase.FenWriter
import com.example.myapplication.domain.model.GameState
import com.example.myapplication.domain.model.Move
import com.example.myapplication.domain.usecase.PgnParser
import com.example.myapplication.domain.usecase.PgnWriter
import kotlinx.coroutines.flow.Flow

class SavedGamesRepositoryImpl(private val dao: SavedGameDao) : SavedGamesRepository {

    override suspend fun save(
        gameId: Long?, history: List<Pair<GameState, Move?>>, gameMode: GameMode, gameResult : GameResult?
    ): Long {
        val moves = history.mapNotNull { it.second }
        val pgn = PgnWriter.write(moves)
        val (opponentType, aiColor, difficulty) = gameMode.toEntityFields()
        val finalState = history.last().first

        val isFinished = gameResult != null
        val entity = SavedGameEntity(
            id = gameId ?: 0,
            pgn = pgn,
            finalPositionFen = FenWriter.writePiecePlacement(finalState.board),
            opponentType = opponentType,
            aiColor = aiColor,
            difficulty = difficulty,
            moveCount = moves.size,
            lastPlayedAt = System.currentTimeMillis(),
            whiteLabel = labelFor(Color.WHITE, gameMode),
            blackLabel = labelFor(Color.BLACK, gameMode),
            isFinished = isFinished,
        )
        val insertedId = dao.upsert(entity)
        return gameId ?: insertedId
    }

    private fun labelFor(color: Color, mode: GameMode): String = when (mode) {
        GameMode.LocalTwoPlayer -> "Player"
        is GameMode.VsAi -> if (mode.aiColor == color) "Computer (${mode.difficulty.name.lowercase()})" else "You"
    }

    override suspend fun load(id: Long): SavedGame? {
        val entity = dao.getById(id) ?: return null
        return SavedGame(id = entity.id, history = PgnParser.parseHistory(entity.pgn), gameMode = entity.toGameMode())
    }

    override fun observeSummaries(): Flow<List<SavedGameSummary>> = dao.observeSummaries()

    override suspend fun delete(id: Long) = dao.delete(id)

}