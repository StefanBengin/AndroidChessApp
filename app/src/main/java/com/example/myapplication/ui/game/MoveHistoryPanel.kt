package com.example.myapplication.ui.game

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MoveHistoryPanel(moveHistorySan: List<String>, modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()
    LaunchedEffect(moveHistorySan.size) {
        if (moveHistorySan.isNotEmpty()) listState.animateScrollToItem(moveHistorySan.size - 1)
    }

    LazyRow(state = listState, modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        items(moveHistorySan.chunked(2).size) { rowIndex ->
            val pair = moveHistorySan.chunked(2)[rowIndex]
            Row(Modifier.padding(end = 12.dp)) {
                Text("${rowIndex + 1}.", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.width(4.dp))
                Text(pair.getOrElse(0) { "" }, style = MaterialTheme.typography.labelSmall)
                if (pair.size > 1) {
                    Spacer(Modifier.width(6.dp))
                    Text(pair[1], style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}