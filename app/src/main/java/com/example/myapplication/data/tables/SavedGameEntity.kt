package com.example.myapplication.data.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_games")
data class SavedGameEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pgn: String,
    val finalPositionFen: String,
    val opponentType: String,      // "LOCAL" or "AI" — stored as plain strings, see note below
    val aiColor: String?,          // "WHITE"/"BLACK", null if opponentType == LOCAL
    val difficulty: String?,       // "MEDIUM" etc., null if opponentType == LOCAL
    val moveCount: Int,
    val lastPlayedAt: Long,        // epoch millis
    val whiteLabel: String,        // denormalized for list rendering — "You" / "Computer (Medium)"
    val blackLabel: String,
    val isFinished: Boolean = false // true once checkmate/stalemate/draw reached, so SavedGamesScreen can filter
)