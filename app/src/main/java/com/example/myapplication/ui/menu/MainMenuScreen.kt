package com.example.myapplication.ui.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.navigation.Screen

@Composable
fun MainMenuScreen(
    onNewGame: () -> Unit,
    onLoadGame: () -> Unit,
    onImportPgn : () -> Unit,
    onSettings: () -> Unit,
    hasSavedGames: Boolean = false
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Chess", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(48.dp))

        Button(onClick = onNewGame, modifier = Modifier.fillMaxWidth(0.7f)) {
            Text("New Game")
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onLoadGame, enabled = hasSavedGames, modifier = Modifier.fillMaxWidth(0.7f)) {
            Text("Continue")
        }
        TextButton(onClick = onImportPgn) {
            Text("Import PGN")
        }

        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onSettings, modifier = Modifier.fillMaxWidth(0.7f)) {
            Text("Settings")
        }

    }
}