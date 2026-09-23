package com.example.myapplication.ui.savedgames

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.SavedGameSummary
import com.example.myapplication.data.repository.SavedGamesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SavedGamesViewModel(
    private val repository: SavedGamesRepository
) : ViewModel() {

    val uiState: StateFlow<SavedGamesUiState> =
        repository.observeSummaries()
            .map { summaries -> SavedGamesUiState(games = summaries, isLoading = false) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SavedGamesUiState(games = emptyList(), isLoading = true)
            )

    fun deleteGame(id: Long) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }

}

data class SavedGamesUiState(
    val games: List<SavedGameSummary>,
    val isLoading: Boolean
)