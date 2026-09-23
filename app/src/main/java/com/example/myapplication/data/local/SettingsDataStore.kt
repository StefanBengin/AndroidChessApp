package com.example.myapplication.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.domain.ai.Difficulty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val BOARD_THEME = stringPreferencesKey("board_theme")
        val PIECE_SET = stringPreferencesKey("piece_set")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val SHOW_LEGAL_MOVES = booleanPreferencesKey("show_legal_moves")
        val SHOW_COORDINATES = booleanPreferencesKey("show_coordinates")
        val CONFIRM_MOVES = booleanPreferencesKey("confirm_moves")
        val AUTO_QUEEN = booleanPreferencesKey("auto_queen")
        val DEFAULT_DIFFICULTY = stringPreferencesKey("default_difficulty")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            boardTheme = prefs[Keys.BOARD_THEME]?.let { BoardTheme.valueOf(it) } ?: BoardTheme.CLASSIC,
            pieceSet = prefs[Keys.PIECE_SET]?.let { PieceSet.valueOf(it) } ?: PieceSet.STANDARD,
            soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
            hapticsEnabled = prefs[Keys.HAPTICS_ENABLED] ?: true,
            showLegalMoves = prefs[Keys.SHOW_LEGAL_MOVES] ?: true,
            showCoordinates = prefs[Keys.SHOW_COORDINATES] ?: true,
            confirmMoves = prefs[Keys.CONFIRM_MOVES] ?: false,
            autoQueen = prefs[Keys.AUTO_QUEEN] ?: false,
            defaultDifficulty = prefs[Keys.DEFAULT_DIFFICULTY]?.let { Difficulty.valueOf(it) } ?: Difficulty.MEDIUM
        )
    }

    suspend fun setBoardTheme(theme: BoardTheme) = update(Keys.BOARD_THEME, theme.name)
    suspend fun setPieceSet(set: PieceSet) = update(Keys.PIECE_SET, set.name)
    suspend fun setSoundEnabled(enabled: Boolean) = update(Keys.SOUND_ENABLED, enabled)
    suspend fun setHapticsEnabled(enabled: Boolean) = update(Keys.HAPTICS_ENABLED, enabled)
    suspend fun setShowLegalMoves(show: Boolean) = update(Keys.SHOW_LEGAL_MOVES, show)
    suspend fun setShowCoordinates(show: Boolean) = update(Keys.SHOW_COORDINATES, show)
    suspend fun setConfirmMoves(confirm: Boolean) = update(Keys.CONFIRM_MOVES, confirm)
    suspend fun setAutoQueen(auto: Boolean) = update(Keys.AUTO_QUEEN, auto)
    suspend fun setDefaultDifficulty(difficulty: Difficulty) = update(Keys.DEFAULT_DIFFICULTY, difficulty.name)

    private suspend fun <T> update(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { it[key] = value }
    }
}

data class AppSettings(
    val boardTheme: BoardTheme,
    val pieceSet: PieceSet,
    val soundEnabled: Boolean,
    val hapticsEnabled: Boolean,
    val showLegalMoves: Boolean,
    val showCoordinates: Boolean,
    val confirmMoves: Boolean,
    val autoQueen: Boolean,
    val defaultDifficulty: Difficulty
)

enum class BoardTheme { CLASSIC, BLUE, GRAY, WALNUT }

enum class PieceSet(val assetPrefix: String, val displayName: String) {
    STANDARD("standard", "Standard"),
    ALT_1("alt1", "Alternate 1"),

    ALT_2("alt2", "Alternate 2")
}

data class GameplaySettings(
    val autoQueen: Boolean = false,
    val confirmMoves: Boolean = false
)
