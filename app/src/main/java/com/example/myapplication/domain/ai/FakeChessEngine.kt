package com.example.myapplication.domain.ai

import com.example.myapplication.domain.engine.MoveGenerator
import com.example.myapplication.domain.model.GameState
import com.example.myapplication.domain.model.Move
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class FakeChessEngine(
    private val moveSelector: (List<Move>) -> Move = { it.random() },
    private val simulatedDelayMs: Long = 400L
) : ChessEngine {
    override suspend fun findBestMove(state: GameState, difficulty: Difficulty): Move {
        delay(simulatedDelayMs.milliseconds)
        return moveSelector(MoveGenerator.legalMoves(state))
    }
    override fun close() {}
}