package com.example.myapplication

import android.content.Context
import androidx.room.Room
import com.example.myapplication.data.database.ChessDatabase
import com.example.myapplication.data.local.SettingsDataStore
import com.example.myapplication.data.repository.SavedGamesRepository
import com.example.myapplication.data.repository.SavedGamesRepositoryImpl
import com.example.myapplication.domain.ai.ChessEngine
import com.example.myapplication.domain.ai.FakeChessEngine
import com.example.myapplication.domain.ai.StockfishEngine

class AppContainer(private val context: Context) {

    private val database: ChessDatabase by lazy {
        Room.databaseBuilder(context, ChessDatabase::class.java, "chess.db").build()
    }

    val savedGamesRepository: SavedGamesRepository by lazy {
        SavedGamesRepositoryImpl(database.savedGameDao())
    }

    private var chessEngine: ChessEngine? = null

    fun getOrCreateChessEngine(): ChessEngine {
        return chessEngine ?: StockfishEngine(context).also { chessEngine = it }
        //return chessEngine ?: FakeChessEngine().also { chessEngine = it }
    }

    fun closeChessEngine() {
        chessEngine?.close()
        chessEngine = null
    }

    val settingsDataStore: SettingsDataStore by lazy { SettingsDataStore(context) }
}