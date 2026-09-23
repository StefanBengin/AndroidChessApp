package com.example.myapplication.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.local.GameplaySettings
import com.example.myapplication.data.local.SettingsDataStore
import com.example.myapplication.data.repository.SavedGamesRepository
import com.example.myapplication.domain.ai.ChessEngine
import com.example.myapplication.domain.ai.GameSource
import kotlinx.coroutines.flow.map

class GameViewModelFactory(
    private val chessEngine: ChessEngine,
    private val savedGamesRepository: SavedGamesRepository,
    private val source : GameSource,
    private val settingsDataStore: SettingsDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return GameViewModel(
            chessEngine = chessEngine,
            savedGamesRepository = savedGamesRepository,
            source = source,
            gameplaySettingsFlow = settingsDataStore.settings.map {
                GameplaySettings(autoQueen = it.autoQueen, confirmMoves = it.confirmMoves)
            }
        ) as T
    }
}