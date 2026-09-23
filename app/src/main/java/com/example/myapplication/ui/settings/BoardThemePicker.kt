package com.example.myapplication.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.local.BoardTheme
import com.example.myapplication.ui.theme.BoardColors

@Composable
fun BoardThemePicker(selected: BoardTheme, onSelect: (BoardTheme) -> Unit) {
    Column {
        Text("Board theme", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp, 8.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(BoardTheme.entries) { theme ->
                val colors = remember(theme) { BoardColors.forTheme(theme) }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        Modifier
                            .border(if (theme == selected) 2.dp else 0.dp, MaterialTheme.colorScheme.primary)
                            .clickable { onSelect(theme) }
                    ) {
                        Box(Modifier.size(20.dp).background(colors.lightSquare))
                        Box(Modifier.size(20.dp).background(colors.darkSquare))
                    }
                    Text(theme.name.lowercase().replaceFirstChar { it.uppercase() })
                }
            }
        }
    }
}