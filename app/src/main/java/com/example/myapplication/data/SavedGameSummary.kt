package com.example.myapplication.data

data class SavedGameSummary(
    val id: Long,
    val whiteLabel: String,
    val blackLabel: String,
    val moveNumber: Int,
    val lastPlayedAt: Long,
    val finalPositionFen: String
)