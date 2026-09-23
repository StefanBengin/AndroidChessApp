package com.example.myapplication.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.myapplication.data.SavedGameSummary
import com.example.myapplication.data.tables.SavedGameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedGameDao {

    @Query("""
        SELECT id, whiteLabel, blackLabel, moveCount as moveNumber, lastPlayedAt, finalPositionFen
        FROM saved_games
        WHERE isFinished = 0
        ORDER BY lastPlayedAt DESC
    """)
    fun observeSummaries(): Flow<List<SavedGameSummary>>

    @Query("SELECT * FROM saved_games WHERE id = :id")
    suspend fun getById(id: Long): SavedGameEntity?

    @Upsert
    suspend fun upsert(entity: SavedGameEntity): Long

    @Query("DELETE FROM saved_games WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM saved_games WHERE isFinished = 1 AND lastPlayedAt < :cutoff")
    suspend fun deleteFinishedOlderThan(cutoff: Long)
}