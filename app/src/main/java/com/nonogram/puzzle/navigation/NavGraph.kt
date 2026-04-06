package com.nonogram.puzzle.navigation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nonogram.puzzle.data.model.PuzzleCategory
import com.nonogram.puzzle.ui.screens.CategoryLevelScreen
import com.nonogram.puzzle.ui.screens.CategoryPickerScreen
import com.nonogram.puzzle.ui.screens.DailyScreen
import com.nonogram.puzzle.ui.screens.GameScreen
import com.nonogram.puzzle.ui.screens.HomeScreen
import com.nonogram.puzzle.ui.screens.LeaderboardScreen
import com.nonogram.puzzle.ui.screens.SettingsScreen
import com.nonogram.puzzle.ui.screens.TutorialScreen
import com.nonogram.puzzle.ui.screens.WinScreen
import com.nonogram.puzzle.utils.PuzzleData
import com.nonogram.puzzle.viewmodel.DailyViewModel
import com.nonogram.puzzle.viewmodel.GameViewModel
import com.nonogram.puzzle.viewmodel.HomeViewModel
import com.nonogram.puzzle.viewmodel.SettingsViewModel
import com.nonogram.puzzle.viewmodel.TutorialViewModel

@Composable
fun NonogramNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    val homeViewModel: HomeViewModel = viewModel()

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Tutorial.route) {
            TutorialScreen(
                onFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Tutorial.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                onPlayClick = {
                    navController.navigate(Screen.PlayCategories.route)
                },
                onDailyClick = { navController.navigate(Screen.Daily.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onLeaderboardClick = {
                    navController.navigate(Screen.Leaderboard.route)
                }
            )
        }

        composable(Screen.PlayCategories.route) {
            CategoryPickerScreen(
                homeViewModel = homeViewModel,
                onCategorySelected = { cat ->
                    navController.navigate(Screen.Category.createRoute(cat))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Category.route,
            arguments = listOf(
                navArgument("categoryName") { type = NavType.StringType }
            )
        ) { backStack ->
            val catName = backStack.arguments?.getString("categoryName") ?: "ANIMALS"
            val category = PuzzleCategory.valueOf(catName)
            CategoryLevelScreen(
                category = category,
                homeViewModel = homeViewModel,
                onLevelSelected = { puzzleId ->
                    navController.navigate(Screen.Game.createRoute(puzzleId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(
                homeViewModel = homeViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(
                navArgument("puzzleId") { type = NavType.IntType },
                navArgument("isDaily") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStack ->
            val puzzleId = backStack.arguments?.getInt("puzzleId") ?: 1001
            val isDaily = backStack.arguments?.getBoolean("isDaily") ?: false
            val gameViewModel: GameViewModel = viewModel()
            GameScreen(
                puzzleId = puzzleId,
                isDaily = isDaily,
                viewModel = gameViewModel,
                onBack = { navController.popBackStack() },
                onWin = { time, errors ->
                    navController.navigate(
                        Screen.Win.createRoute(puzzleId, time, errors, isDaily)
                    ) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Win.route,
            arguments = listOf(
                navArgument("puzzleId") { type = NavType.IntType },
                navArgument("time") { type = NavType.LongType },
                navArgument("errors") { type = NavType.IntType },
                navArgument("isDaily") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStack ->
            val puzzleId = backStack.arguments?.getInt("puzzleId") ?: 1001
            val time = backStack.arguments?.getLong("time") ?: 0L
            val errors = backStack.arguments?.getInt("errors") ?: 0
            val isDaily = backStack.arguments?.getBoolean("isDaily") ?: false
            val puzzle = PuzzleData.getById(puzzleId)
            WinScreen(
                puzzleId = puzzleId,
                time = time,
                errors = errors,
                isDailyPuzzle = isDaily,
                onNextPuzzle = {
                    homeViewModel.refreshCompletedIds()
                    if (isDaily) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                        return@WinScreen
                    }
                    val cat = puzzle?.category ?: PuzzleCategory.ANIMALS
                    val catPuzzles = PuzzleData.getByCategory(cat)
                    val currentIndex = catPuzzles.indexOfFirst { it.id == puzzleId }
                    val nextPuzzle = catPuzzles.getOrNull(currentIndex + 1)
                    if (nextPuzzle != null) {
                        navController.navigate(Screen.Game.createRoute(nextPuzzle.id)) {
                            popUpTo(Screen.Category.createRoute(cat)) {
                                inclusive = false
                            }
                        }
                    } else {
                        navController.navigate(Screen.Category.createRoute(cat)) {
                            popUpTo(Screen.Category.createRoute(cat)) { inclusive = true }
                        }
                    }
                },
                onHome = {
                    homeViewModel.refreshCompletedIds()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Daily.route) {
            val dailyViewModel: DailyViewModel = viewModel()
            DailyScreen(
                viewModel = dailyViewModel,
                onBack = { navController.popBackStack() },
                onPlayDaily = {
                    navController.navigate(
                        Screen.Game.createRoute(
                            dailyViewModel.dailyPuzzleId,
                            isDaily = true
                        )
                    )
                }
            )
        }

        composable(Screen.Settings.route) {
            val activity = LocalContext.current as ComponentActivity
            val tutorialViewModel: TutorialViewModel =
                viewModel(viewModelStoreOwner = activity)
            val settingsViewModel: SettingsViewModel = viewModel()
            SettingsScreen(
                viewModel = settingsViewModel,
                onBack = { navController.popBackStack() },
                onReplayTutorial = {
                    settingsViewModel.resetTutorial()
                    tutorialViewModel.restartSession()
                    navController.navigate(Screen.Tutorial.route) {
                        popUpTo(Screen.Settings.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
