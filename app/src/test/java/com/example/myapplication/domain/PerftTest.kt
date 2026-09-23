package com.example.myapplication.domain

import com.example.myapplication.domain.engine.MoveGenerator
import com.example.myapplication.domain.model.*
import org.junit.Test

private fun Perft(state: GameState, depth: Int): Long {
    if (depth == 0) return 1L
    var nodes = 0L
    for (move in MoveGenerator.legalMoves(state)) {
        nodes += Perft(state.applyMove(move), depth - 1)
    }
    return nodes
}

class PerftTest {
    @Test
    fun startingPositionTest() {
        val startingState = GameState.newGame()
        assert(Perft(startingState, 0) == 1L)
        assert(Perft(startingState, 1) == 20L)
        assert(Perft(startingState, 2) == 400L)
        assert(Perft(startingState, 3) == 8902L)
        assert(Perft(startingState, 4) == 197281L)
        assert(Perft(startingState, 5) == 4865609L)
    }
}