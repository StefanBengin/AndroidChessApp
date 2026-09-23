package com.example.myapplication.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.local.SettingsDataStore
import com.example.myapplication.data.repository.SavedGamesRepository
import com.example.myapplication.ui.savedgames.SavedGamesViewModel

class SettingsViewModelFactory(
    private val dataStore : SettingsDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SettingsViewModel(dataStore) as T
    }
}