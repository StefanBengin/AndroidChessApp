package com.example.myapplication.ui.importpgn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.repository.SavedGamesRepository
import com.example.myapplication.ui.savedgames.SavedGamesViewModel

class ImportPgnViewModelFactory(
    private val repository: SavedGamesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ImportPgnViewModel(repository) as T
    }
}