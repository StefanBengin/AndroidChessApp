package com.example.myapplication.ui.navigation

import com.example.myapplication.domain.ai.Difficulty
import com.example.myapplication.domain.ai.GameMode
import com.example.myapplication.domain.model.Color

// ui/navigation/Screen.kt

sealed class Screen(val route: String) {
    object MainMenu : Screen("main_menu")
    object NewGameSetup : Screen("new_game_setup")
    object SavedGames : Screen("saved_games")
    object Settings : Screen("settings")

    object NewGame : Screen("game/new/{gameModeArg}") {
        fun route(gameMode: GameMode) = "game/new/${encodeGameMode(gameMode)}"
    }

    object ResumeGame : Screen("game/resume/{savedGameId}") {
        fun route(savedGameId: Long) = "game/resume/$savedGameId"
    }

    object ImportPgn : Screen("import_pgn")
}

fun encodeGameMode(mode: GameMode): String = when (mode) {
    GameMode.LocalTwoPlayer -> "local"
    is GameMode.VsAi -> "ai_${mode.aiColor}_${mode.difficulty}"
}

fun decodeGameMode(arg: String): GameMode {
    if (arg == "local") return GameMode.LocalTwoPlayer
    val parts = arg.split("_")
    return GameMode.VsAi(Color.valueOf(parts[1]), Difficulty.valueOf(parts[2]))
}
