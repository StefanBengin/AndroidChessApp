package com.example.myapplication.domain

import com.example.myapplication.domain.engine.MoveGenerator
import com.example.myapplication.domain.model.GameState
import com.example.myapplication.domain.model.Move
import com.example.myapplication.domain.usecase.PgnParser
import com.example.myapplication.domain.usecase.PgnWriter
import junit.framework.TestCase.assertEquals
import org.junit.Test
import kotlin.random.Random

class PgnTest{
    @Test
    fun pgnRoundTrip_preservesMoves() {
        var state = GameState.newGame()
        val originalMoves = mutableListOf<Move>()
        val rng = Random(42) // fixed seed for reproducibility

        repeat(40) {
            val legal = MoveGenerator.legalMoves(state)
            if (legal.isEmpty()) return@repeat // game ended early
            val move = legal[rng.nextInt(legal.size)]
            originalMoves.add(move)
            state = state.applyMove(move)
        }

        val pgn = PgnWriter.write(originalMoves)
        val parsedMoves = PgnParser.parseMoveList(pgn)

        assertEquals(originalMoves, parsedMoves)
    }
}