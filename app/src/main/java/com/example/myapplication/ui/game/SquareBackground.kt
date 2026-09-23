package com.example.myapplication.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as UiColor
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.model.Piece
import com.example.myapplication.ui.common.PieceImage
import com.example.myapplication.ui.theme.*

@Composable
fun SquareBackground(
    size: Dp,
    colors: BoardColorScheme,
    isLight: Boolean,
    isSelected: Boolean,
    isLegalDestination: Boolean,
    hasLegalCapture: Boolean,
    isLastMoveEndpoint: Boolean,
    isCheckedKing: Boolean,
    coordinateLabel: String?,
    onTap: () -> Unit
) {
    val baseColor = if (isLight) colors.lightSquare else colors.darkSquare
    val overlayColor = when {
        isCheckedKing -> colors.checkHighlight
        isSelected -> colors.selectedHighlight
        isLastMoveEndpoint -> colors.lastMoveHighlight
        else -> null
    }

    Box(
        modifier = Modifier
            .size(size)
            .background(baseColor)
            .then(if (overlayColor != null) Modifier.background(overlayColor.copy(alpha = 0.4f)) else Modifier)
            .clickable(onClick = onTap)
    ) {
        if (isLegalDestination) {
            if (!hasLegalCapture) {
                Box(
                    Modifier.align(Alignment.Center).fillMaxSize(0.3f)
                        .background(colors.legalMoveDot.copy(alpha = 0.5f), CircleShape)
                )
            } else {
                Box(
                    Modifier.fillMaxSize()
                        .border(3.dp, colors.legalMoveDot.copy(alpha = 0.6f), RectangleShape)
                )
            }
        }

        coordinateLabel?.let { label ->
            Text(
                text = label,
                fontSize = (size.value * 0.16f).sp,
                color = (if (isLight) colors.darkSquare else colors.lightSquare).copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.BottomStart).padding(2.dp)
            )
        }
    }
}