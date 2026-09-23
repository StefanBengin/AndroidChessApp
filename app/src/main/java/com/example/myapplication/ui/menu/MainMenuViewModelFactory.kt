package com.example.myapplication.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.repository.SavedGamesRepository

class MainMenuViewModelFactory(
    private val repository: SavedGamesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MainMenuViewModel(repository) as T
    }
}