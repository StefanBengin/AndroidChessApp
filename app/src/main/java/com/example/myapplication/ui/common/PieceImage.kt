package com.example.myapplication.ui.common

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.myapplication.R
import com.example.myapplication.data.local.PieceSet
import com.example.myapplication.domain.model.Color
import com.example.myapplication.domain.model.Piece
import com.example.myapplication.domain.model.PieceType

@Composable
fun PieceImage(piece: Piece, pieceSet: PieceSet, modifier: Modifier = Modifier) {
    val resId = pieceDrawableRes(piece, pieceSet)
    Image(
        painter = painterResource(resId),
        contentDescription = "${piece.color} ${piece.type}",
        modifier = modifier
    )
}

private val pieceDrawables: Map<PieceSet, Map<Pair<Color, PieceType>, Int>> = mapOf(
    PieceSet.STANDARD to mapOf(
        (Color.WHITE to PieceType.PAWN) to R.drawable.alpha_wp,
        (Color.WHITE to PieceType.KNIGHT) to R.drawable.alpha_wn,
        (Color.WHITE to PieceType.BISHOP) to R.drawable.alpha_wb,
        (Color.WHITE to PieceType.ROOK) to R.drawable.alpha_wr,
        (Color.WHITE to PieceType.QUEEN) to R.drawable.alpha_wq,
        (Color.WHITE to PieceType.KING) to R.drawable.alpha_wk,
        (Color.BLACK to PieceType.PAWN) to R.drawable.alpha_bp,
        (Color.BLACK to PieceType.KNIGHT) to R.drawable.alpha_bn,
        (Color.BLACK to PieceType.BISHOP) to R.drawable.alpha_bb,
        (Color.BLACK to PieceType.ROOK) to R.drawable.alpha_br,
        (Color.BLACK to PieceType.QUEEN) to R.drawable.alpha_bq,
        (Color.BLACK to PieceType.KING) to R.drawable.alpha_bk,
    ),
    PieceSet.ALT_1 to mapOf(
        (Color.WHITE to PieceType.PAWN) to R.drawable.cburnett_wp,
        (Color.WHITE to PieceType.KNIGHT) to R.drawable.cburnett_wn,
        (Color.WHITE to PieceType.BISHOP) to R.drawable.cburnett_wb,
        (Color.WHITE to PieceType.ROOK) to R.drawable.cburnett_wr,
        (Color.WHITE to PieceType.QUEEN) to R.drawable.cburnett_wq,
        (Color.WHITE to PieceType.KING) to R.drawable.cburnett_wk,
        (Color.BLACK to PieceType.PAWN) to R.drawable.cburnett_bp,
        (Color.BLACK to PieceType.KNIGHT) to R.drawable.cburnett_bn,
        (Color.BLACK to PieceType.BISHOP) to R.drawable.cburnett_bb,
        (Color.BLACK to PieceType.ROOK) to R.drawable.cburnett_br,
        (Color.BLACK to PieceType.QUEEN) to R.drawable.cburnett_bq,
        (Color.BLACK to PieceType.KING) to R.drawable.cburnett_bk,
    ),
    PieceSet.ALT_2 to mapOf(
        (Color.WHITE to PieceType.PAWN) to R.drawable.merida_wp,
        (Color.WHITE to PieceType.KNIGHT) to R.drawable.merida_wn,
        (Color.WHITE to PieceType.BISHOP) to R.drawable.merida_wb,
        (Color.WHITE to PieceType.ROOK) to R.drawable.merida_wr,
        (Color.WHITE to PieceType.QUEEN) to R.drawable.merida_wq,
        (Color.WHITE to PieceType.KING) to R.drawable.merida_wk,
        (Color.BLACK to PieceType.PAWN) to R.drawable.merida_bp,
        (Color.BLACK to PieceType.KNIGHT) to R.drawable.merida_bn,
        (Color.BLACK to PieceType.BISHOP) to R.drawable.merida_bb,
        (Color.BLACK to PieceType.ROOK) to R.drawable.merida_br,
        (Color.BLACK to PieceType.QUEEN) to R.drawable.merida_bq,
        (Color.BLACK to PieceType.KING) to R.drawable.merida_bk,
    ),
)

private fun pieceDrawableRes(piece: Piece, pieceSet: PieceSet): Int =
    pieceDrawables.getValue(pieceSet).getValue(piece.color to piece.type)