package com.example.myapplication.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.local.BoardTheme
import com.example.myapplication.data.local.PieceSet
import com.example.myapplication.domain.ai.Difficulty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onBack: () -> Unit) {
    val settings by viewModel.settings.collectAsState()

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Settings") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )
    }) { padding ->
        Column(Modifier
            .padding(padding)
            .verticalScroll(rememberScrollState())) {
            //SettingsSectionHeader("Board")
            //SettingsDropdownRow("Theme", settings.boardTheme, BoardTheme.entries, viewModel::setBoardTheme)
            //SettingsDropdownRow("Piece set", settings.pieceSet, PieceSet.entries, viewModel::setPieceSet)
            SettingsSwitchRow("Show coordinates", settings.showCoordinates, viewModel::setShowCoordinates)
            SettingsSwitchRow("Show legal moves", settings.showLegalMoves, viewModel::setShowLegalMoves)

            //SettingsSectionHeader("Gameplay")
            //SettingsSwitchRow("Confirm moves before playing", settings.confirmMoves, viewModel::setConfirmMoves)
            SettingsSwitchRow("Always promote to queen", settings.autoQueen, viewModel::setAutoQueen)
            //SettingsDropdownRow("Default AI difficulty", settings.defaultDifficulty, Difficulty.entries, viewModel::setDefaultDifficulty)

            //SettingsSectionHeader("Feedback")
            SettingsSwitchRow("Sound effects", settings.soundEnabled, viewModel::setSoundEnabled)
            //SettingsSwitchRow("Haptic feedback", settings.hapticsEnabled, viewModel::setHapticsEnabled)

            BoardThemePicker(settings.boardTheme, onSelect = viewModel::setBoardTheme)
            PieceSetPicker(settings.pieceSet, onSelect = viewModel::setPieceSet)
        }
    }
}

@Composable
private fun SettingsSwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}