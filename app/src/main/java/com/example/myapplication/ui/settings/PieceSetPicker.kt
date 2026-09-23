package com.example.myapplication.ui.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.local.PieceSet
import com.example.myapplication.domain.model.Color
import com.example.myapplication.domain.model.Piece
import com.example.myapplication.domain.model.PieceType
import com.example.myapplication.ui.common.PieceImage

@Composable
fun PieceSetPicker(selected: PieceSet, onSelect: (PieceSet) -> Unit) {
    Column {
        Text("Piece set", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp, 8.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(PieceSet.entries) { set ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onSelect(set) }
                        .border(
                            width = if (set == selected) 2.dp else 0.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    Row {
                        PieceImage(Piece(PieceType.KING, Color.WHITE), set, Modifier.size(32.dp))
                        PieceImage(Piece(PieceType.KNIGHT, Color.BLACK), set, Modifier.size(32.dp))
                    }
                    Text(set.displayName, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}