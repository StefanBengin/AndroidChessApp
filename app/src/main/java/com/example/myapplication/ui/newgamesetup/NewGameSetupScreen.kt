package com.example.myapplication.ui.newgamesetup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.ai.Difficulty
import com.example.myapplication.domain.ai.GameMode
import com.example.myapplication.domain.model.Color
import com.example.myapplication.ui.settings.SettingsViewModel

@Composable
fun NewGameSetupScreen(settingsViewModel: SettingsViewModel, onStartGame: (GameMode) -> Unit) {
    val settings by settingsViewModel.settings.collectAsState()

    var opponentType by remember { mutableStateOf(OpponentType.AI) }
    var aiColor by remember { mutableStateOf(Color.BLACK) }
    var difficulty by remember { mutableStateOf<Difficulty?>(null) } // null = "not yet initialized from settings"

    LaunchedEffect(settings.defaultDifficulty) {
        if (difficulty == null) difficulty = settings.defaultDifficulty
    }

    val selectedDifficulty = difficulty ?: settings.defaultDifficulty

    Column(modifier = Modifier.padding(24.dp)) {
        Text("Opponent", style = MaterialTheme.typography.titleMedium)
        SingleChoiceSegmentedButtonRow {
            SegmentedButton(
                selected = opponentType == OpponentType.LOCAL,
                onClick = { opponentType = OpponentType.LOCAL },
                shape = SegmentedButtonDefaults.itemShape(0, 2)
            ) { Text("Pass & Play") }
            SegmentedButton(
                selected = opponentType == OpponentType.AI,
                onClick = { opponentType = OpponentType.AI },
                shape = SegmentedButtonDefaults.itemShape(1, 2)
            ) { Text("Computer") }
        }

        if (opponentType == OpponentType.AI) {
            Spacer(Modifier.height(24.dp))
            Text("Play as", style = MaterialTheme.typography.titleMedium)
            SingleChoiceSegmentedButtonRow {
                SegmentedButton(
                    selected = aiColor == Color.BLACK, // I play white, AI plays black
                    onClick = { aiColor = Color.BLACK },
                    shape = SegmentedButtonDefaults.itemShape(0, 2)
                ) { Text("White") }
                SegmentedButton(
                    selected = aiColor == Color.WHITE,
                    onClick = { aiColor = Color.WHITE },
                    shape = SegmentedButtonDefaults.itemShape(1, 2)
                ) { Text("Black") }
            }

            if (opponentType == OpponentType.AI) {
                Spacer(Modifier.height(24.dp))
                Text("Difficulty", style = MaterialTheme.typography.titleMedium)
                Column(Modifier.selectableGroup()) {
                    Difficulty.entries.forEach { level ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedDifficulty == level,
                                onClick = { difficulty = level }
                            )
                            Text(level.name.lowercase().replaceFirstChar { it.uppercase() })
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))
        Button(
            onClick = {
                val mode = if (opponentType == OpponentType.LOCAL) GameMode.LocalTwoPlayer
                else GameMode.VsAi(aiColor, selectedDifficulty)
                onStartGame(mode)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Game")
        }
    }
}

private enum class OpponentType { LOCAL, AI }