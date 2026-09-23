package com.example.myapplication.ui.navigation

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.LocalAppContainer
import com.example.myapplication.data.repository.SavedGame
import com.example.myapplication.domain.ai.GameMode
import com.example.myapplication.domain.ai.GameSource
import com.example.myapplication.ui.game.GameScreen
import com.example.myapplication.ui.game.GameViewModelFactory
import com.example.myapplication.ui.importpgn.ImportPgnScreen
import com.example.myapplication.ui.importpgn.ImportPgnViewModel
import com.example.myapplication.ui.importpgn.ImportPgnViewModelFactory
import com.example.myapplication.ui.menu.MainMenuScreen
import com.example.myapplication.ui.menu.MainMenuViewModel
import com.example.myapplication.ui.menu.MainMenuViewModelFactory
import com.example.myapplication.ui.newgamesetup.NewGameSetupScreen
import com.example.myapplication.ui.savedgames.SavedGamesScreen
import com.example.myapplication.ui.savedgames.SavedGamesViewModel
import com.example.myapplication.ui.savedgames.SavedGamesViewModelFactory
import com.example.myapplication.ui.settings.SettingsScreen
import com.example.myapplication.ui.settings.SettingsViewModel
import com.example.myapplication.ui.settings.SettingsViewModelFactory

@Composable
fun ChessNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController, startDestination = Screen.MainMenu.route) {
        composable(Screen.MainMenu.route) {
            val container = LocalAppContainer.current
            val viewModel: MainMenuViewModel = viewModel(
                factory = MainMenuViewModelFactory(container.savedGamesRepository)
            )
            val hasSavedGames by viewModel.hasSavedGames.collectAsState()

            MainMenuScreen(
                hasSavedGames = hasSavedGames,
                onNewGame = { navController.navigate(Screen.NewGameSetup.route) },
                onLoadGame = { navController.navigate(Screen.SavedGames.route) },
                onImportPgn = {navController.navigate(Screen.ImportPgn.route) },
                onSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.NewGameSetup.route) {
            val container = LocalAppContainer.current
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(container.settingsDataStore)
            )

            NewGameSetupScreen(
                settingsViewModel = settingsViewModel,
                onStartGame = { gameMode ->
                    navController.navigate(Screen.NewGame.route(gameMode)) {
                        popUpTo(Screen.MainMenu.route)
                    }
                }
            )
        }

        composable(Screen.SavedGames.route) {
            val container = LocalAppContainer.current
            val savedGamesViewModel: SavedGamesViewModel = viewModel(
                factory = SavedGamesViewModelFactory(container.savedGamesRepository)
            )
            val settingsViewModel : SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(container.settingsDataStore)
            )
            SavedGamesScreen(
                savedGamesViewModel = savedGamesViewModel,
                settingsViewModel = settingsViewModel,
                onGameSelected = { savedGameId ->
                    navController.navigate(Screen.ResumeGame.route(savedGameId)) {
                        popUpTo(Screen.MainMenu.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.NewGame.route,
            arguments = listOf(navArgument("gameModeArg") { type = NavType.StringType })
        ) { backStackEntry ->
            val container = LocalAppContainer.current
            val gameModeArg = backStackEntry.arguments!!.getString("gameModeArg")!!
            val gameMode = remember(gameModeArg) { decodeGameMode(gameModeArg) }

            GameScreen(
                viewModel = viewModel(
                    factory = GameViewModelFactory(
                        source = GameSource.NewGame(gameMode),
                        chessEngine = container.getOrCreateChessEngine(),
                        savedGamesRepository = container.savedGamesRepository,
                        settingsDataStore = container.settingsDataStore
                    )
                ),
                settingsViewModel = viewModel(factory = SettingsViewModelFactory(container.settingsDataStore)),
                onExitGame = { navController.popBackStack(Screen.MainMenu.route, inclusive = false) },
                onRematch = { gameMode ->
                    navController.navigate(Screen.NewGame.route(gameMode)) {
                        popUpTo(Screen.MainMenu.route)
                    }
                }
            )
        }

        composable(
            route = Screen.ResumeGame.route,
            arguments = listOf(navArgument("savedGameId") { type = NavType.LongType })
        ) { backStackEntry ->
            val container = LocalAppContainer.current
            val savedGameId = backStackEntry.arguments!!.getLong("savedGameId")

            val loadedGame by produceState<SavedGame?>(initialValue = null, savedGameId) {
                value = container.savedGamesRepository.load(savedGameId)
            }

            when (val loaded = loadedGame) {
                null -> Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                else -> GameScreen(
                    viewModel = viewModel(
                        factory = GameViewModelFactory(
                            source = GameSource.Resume(loaded),
                            chessEngine = container.getOrCreateChessEngine(),
                            savedGamesRepository = container.savedGamesRepository,
                            settingsDataStore = container.settingsDataStore
                        )
                    ),
                    settingsViewModel = viewModel(factory = SettingsViewModelFactory(container.settingsDataStore)),
                    onExitGame = { navController.popBackStack(Screen.MainMenu.route, inclusive = false) },
                    onRematch = { gameMode ->
                        navController.navigate(Screen.NewGame.route(gameMode)) {
                            popUpTo(Screen.MainMenu.route)
                        }
                    }
                )
            }
        }

        composable(Screen.Settings.route) {
            val container = LocalAppContainer.current
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(container.settingsDataStore)
            )
            SettingsScreen(viewModel = settingsViewModel, onBack = { navController.popBackStack() })
        }

        composable(Screen.ImportPgn.route) {
            val container = LocalAppContainer.current
            val viewModel: ImportPgnViewModel = viewModel(
                factory = ImportPgnViewModelFactory(container.savedGamesRepository)
            )

            ImportPgnScreen(
                viewModel = viewModel,
                onImported = { savedGameId ->
                    navController.navigate(Screen.ResumeGame.route(savedGameId)) {
                        popUpTo(Screen.MainMenu.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
