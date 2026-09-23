package com.example.myapplication.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapplication.data.dao.SavedGameDao
import com.example.myapplication.data.tables.SavedGameEntity

@Database(entities = [SavedGameEntity::class], version = 1, exportSchema = true)
abstract class ChessDatabase : RoomDatabase() {
    abstract fun savedGameDao(): SavedGameDao
}

