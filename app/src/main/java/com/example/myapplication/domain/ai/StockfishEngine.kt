package com.example.myapplication.domain.ai

import android.content.Context
import com.example.myapplication.domain.engine.MoveGenerator
import com.example.myapplication.domain.model.GameState
import com.example.myapplication.domain.model.Move
import com.example.myapplication.domain.model.Squares
import com.example.myapplication.domain.usecase.FenWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter

class StockfishEngine(private val context: Context) : ChessEngine {

    private var process: Process? = null
    private var writer: BufferedWriter? = null
    private var reader: BufferedReader? = null
    private val startMutex = Mutex()

    private suspend fun ensureStarted() = startMutex.withLock {
        if (process != null) return@withLock
        withContext(Dispatchers.IO) {
            val binaryPath = "${context.applicationInfo.nativeLibraryDir}/libstockfish.so"
            val proc = ProcessBuilder(binaryPath)
                .redirectErrorStream(true)
                .start()
            process = proc
            writer = proc.outputStream.bufferedWriter()
            reader = proc.inputStream.bufferedReader()

            send("uci")
            awaitLine { it == "uciok" }
            send("isready")
            awaitLine { it == "readyok" }
        }
    }

    override suspend fun findBestMove(state: GameState, difficulty: Difficulty): Move {
        ensureStarted()
        return withContext(Dispatchers.IO) {
            val params = searchParamsFor(difficulty)
            send("setoption name Skill Level value ${params.skillLevel}")
            send("position fen ${FenWriter.write(state)}")
            send("go depth ${params.depth}")

            val bestMoveLine = awaitLine { it.startsWith("bestmove") }
            val uciMove = bestMoveLine.split(" ")[1] // "bestmove e2e4" or "bestmove e7e8q"

            MoveGenerator.legalMoves(state).firstOrNull { it.matchesUci(uciMove) }
                ?: error("Stockfish returned a move not found in legal moves: $uciMove")
        }
    }

    private fun send(command: String) {
        writer?.write(command)
        writer?.newLine()
        writer?.flush()
    }

    private fun awaitLine(predicate: (String) -> Boolean): String {
        while (true) {
            val line = reader?.readLine() ?: error("Stockfish process ended unexpectedly")
            if (predicate(line)) return line
        }
    }

    private data class SearchParams(val depth: Int, val skillLevel: Int)

    private fun searchParamsFor(difficulty: Difficulty): SearchParams = when (difficulty) {
        Difficulty.BEGINNER -> SearchParams(depth = 2, skillLevel = 0)
        Difficulty.EASY -> SearchParams(depth = 4, skillLevel = 5)
        Difficulty.MEDIUM -> SearchParams(depth = 8, skillLevel = 10)
        Difficulty.HARD -> SearchParams(depth = 12, skillLevel = 15)
        Difficulty.EXPERT -> SearchParams(depth = 16, skillLevel = 20)
    }

    override fun close() {
        send("quit")
        process?.destroy()
        process = null
        writer = null
        reader = null
    }
}

private fun Move.matchesUci(uci: String): Boolean {
    val expectedFrom = Squares.toAlgebraic(from)
    val expectedTo = Squares.toAlgebraic(to)
    val promoChar = promotion?.symbol?.lowercaseChar()
    val expected = "$expectedFrom$expectedTo${promoChar ?: ""}"
    return uci == expected
}