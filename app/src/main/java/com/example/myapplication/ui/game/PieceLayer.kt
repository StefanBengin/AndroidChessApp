package com.example.myapplication.ui.game

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import com.example.myapplication.data.local.PieceSet
import com.example.myapplication.domain.model.Color
import com.example.myapplication.domain.model.Move
import com.example.myapplication.domain.model.Piece
import com.example.myapplication.domain.model.Square
import com.example.myapplication.domain.model.Squares
import com.example.myapplication.ui.common.PieceImage
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class PositionedPiece(val id: Long, val piece: Piece, val square: Square)

@Composable
fun PieceLayer(
    positionedPieces: List<PositionedPiece>,
    squareSizeDp: Dp,
    orientation: Color,
    pieceSet: PieceSet
) {
    var previousOrientation by remember { mutableStateOf(orientation) }
    val isFlipTransition = orientation != previousOrientation
    SideEffect { previousOrientation = orientation } // updates AFTER this frame reads it, once per recomposition

    Box(Modifier.fillMaxSize()) {
        for (positioned in positionedPieces) {
            key(positioned.id) {
                val (displayRow, displayCol) = squareToDisplay(positioned.square, orientation)
                val targetOffset = DpOffset(squareSizeDp * displayCol, squareSizeDp * displayRow)
                val animatedOffset = animatedPieceOffset(targetOffset, snap = isFlipTransition)

                PieceImage(
                    piece = positioned.piece,
                    pieceSet = pieceSet,
                    modifier = Modifier.offset(animatedOffset.x, animatedOffset.y).size(squareSizeDp)
                )
            }
        }
    }
}

@Composable
private fun animatedPieceOffset(target: DpOffset, snap: Boolean): DpOffset {
    val xAnim = remember { Animatable(target.x, Dp.VectorConverter) }
    val yAnim = remember { Animatable(target.y, Dp.VectorConverter) }

    LaunchedEffect(target, snap) {
        if (snap) {
            xAnim.snapTo(target.x)
            yAnim.snapTo(target.y)
        } else {
            coroutineScope {
                launch { xAnim.animateTo(target.x, tween(200, easing = FastOutSlowInEasing)) }
                launch { yAnim.animateTo(target.y, tween(200, easing = FastOutSlowInEasing)) }
            }
        }
    }

    return DpOffset(xAnim.value, yAnim.value)
}

private fun squareToDisplay(square: Square, orientation: Color): Pair<Int, Int> {
    val rank = Squares.rank(square)
    val file = Squares.file(square)
    val displayRow = if (orientation == Color.WHITE) 7 - rank else rank
    val displayCol = if (orientation == Color.WHITE) file else 7 - file
    return displayRow to displayCol
}