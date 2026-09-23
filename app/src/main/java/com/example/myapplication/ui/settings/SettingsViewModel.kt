package com.example.myapplication.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.AppSettings
import com.example.myapplication.data.local.BoardTheme
import com.example.myapplication.data.local.PieceSet
import com.example.myapplication.data.local.SettingsDataStore
import com.example.myapplication.domain.ai.Difficulty
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val dataStore: SettingsDataStore) : ViewModel() {

    val settings: StateFlow<AppSettings> = dataStore.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSettings(
            BoardTheme.CLASSIC, PieceSet.STANDARD, soundEnabled = true, hapticsEnabled = true,
            showLegalMoves = true, showCoordinates = true, confirmMoves = false, autoQueen = false,
            defaultDifficulty = Difficulty.MEDIUM
        )
    )

    fun setBoardTheme(theme: BoardTheme) = viewModelScope.launch { dataStore.setBoardTheme(theme) }
    fun setPieceSet(set: PieceSet) = viewModelScope.launch { dataStore.setPieceSet(set) }
    fun setSoundEnabled(enabled: Boolean) = viewModelScope.launch { dataStore.setSoundEnabled(enabled) }
    fun setHapticsEnabled(enabled: Boolean) = viewModelScope.launch { dataStore.setHapticsEnabled(enabled) }
    fun setShowLegalMoves(show: Boolean) = viewModelScope.launch { dataStore.setShowLegalMoves(show) }
    fun setShowCoordinates(show: Boolean) = viewModelScope.launch { dataStore.setShowCoordinates(show) }
    fun setConfirmMoves(confirm: Boolean) = viewModelScope.launch { dataStore.setConfirmMoves(confirm) }
    fun setAutoQueen(auto: Boolean) = viewModelScope.launch { dataStore.setAutoQueen(auto) }
    fun setDefaultDifficulty(d: Difficulty) = viewModelScope.launch { dataStore.setDefaultDifficulty(d) }
}