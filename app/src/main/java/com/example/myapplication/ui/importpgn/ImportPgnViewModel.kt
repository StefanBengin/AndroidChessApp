package com.example.myapplication.ui.importpgn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.SavedGamesRepository
import com.example.myapplication.domain.ai.GameMode
import com.example.myapplication.domain.engine.GameResultDetector
import com.example.myapplication.domain.usecase.PgnParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ImportPgnUiState(
    val pgnText: String = "",
    val isImporting: Boolean = false,
    val errorMessage: String? = null
)

class ImportPgnViewModel(
    private val savedGamesRepository: SavedGamesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportPgnUiState())
    val uiState: StateFlow<ImportPgnUiState> = _uiState.asStateFlow()

    fun onPgnTextChanged(text: String) {
        _uiState.update { it.copy(pgnText = text, errorMessage = null) }
    }

    fun import(onImported: (savedGameId: Long) -> Unit) {
        val pgnText = _uiState.value.pgnText
        if (pgnText.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Paste a PGN to import") }
            return
        }

        _uiState.update { it.copy(isImporting = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val history = withContext(Dispatchers.Default) { PgnParser.parseHistory(pgnText) }
                val id = savedGamesRepository.save(
                    gameId = null,
                    history = history,
                    gameMode = GameMode.LocalTwoPlayer,
                    gameResult = GameResultDetector.detect(history.last().first)
                )
                onImported(id)
            } catch (e: Exception) {
                _uiState.update { it.copy(isImporting = false, errorMessage = "Couldn't read this PGN — check the format and try again") }
            }
        }
    }
}