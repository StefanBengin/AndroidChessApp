package com.example.myapplication.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.myapplication.data.local.BoardTheme

// ui/theme/BoardColors.kt

data class BoardColorScheme(
    val lightSquare: Color,
    val darkSquare: Color,
    val selectedHighlight: Color,
    val lastMoveHighlight: Color,
    val checkHighlight: Color,
    val legalMoveDot: Color
)

object BoardColors {
    private val classic = BoardColorScheme(
        lightSquare = Color(0xFFF0D9B5),
        darkSquare = Color(0xFFB58863),
        selectedHighlight = Color(0xFFFFEB3B),
        lastMoveHighlight = Color(0xFFCDD26A),
        checkHighlight = Color(0xFFE53935),
        legalMoveDot = Color(0xFF000000)
    )
    private val blue = BoardColorScheme(
        lightSquare = Color(0xFFDEE3E6),
        darkSquare = Color(0xFF8CA2AD),
        selectedHighlight = Color(0xFFFFEB3B),
        lastMoveHighlight = Color(0xFFAAC1D3),
        checkHighlight = Color(0xFFE53935),
        legalMoveDot = Color(0xFF000000)
    )
    private val gray = BoardColorScheme(
        lightSquare = Color(0xFFE8E8E8),
        darkSquare = Color(0xFF8A8A8A),
        selectedHighlight = Color(0xFFFFEB3B),
        lastMoveHighlight = Color(0xFFBFBFBF),
        checkHighlight = Color(0xFFE53935),
        legalMoveDot = Color(0xFF000000)
    )
    private val walnut = BoardColorScheme(
        lightSquare = Color(0xFFE8CDA3),
        darkSquare = Color(0xFF7A5230),
        selectedHighlight = Color(0xFFFFEB3B),
        lastMoveHighlight = Color(0xFFC9A96A),
        checkHighlight = Color(0xFFE53935),
        legalMoveDot = Color(0xFF000000)
    )

    fun forTheme(theme: BoardTheme): BoardColorScheme = when (theme) {
        BoardTheme.CLASSIC -> classic
        BoardTheme.BLUE -> blue
        BoardTheme.GRAY -> gray
        BoardTheme.WALNUT -> walnut
    }
}