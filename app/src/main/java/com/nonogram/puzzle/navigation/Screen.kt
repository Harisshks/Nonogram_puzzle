package com.nonogram.puzzle.navigation

import com.nonogram.puzzle.data.model.PuzzleCategory

sealed class Screen(val route: String) {
    object Tutorial : Screen("tutorial")

    object Home : Screen("home")

    /** Full-screen picker with five category cards before level grid */
    object PlayCategories : Screen("play_categories")

    object Category : Screen("category/{categoryName}") {
        fun createRoute(cat: PuzzleCategory) = "category/${cat.name}"
    }

    object Leaderboard : Screen("leaderboard")

    object Game : Screen("game/{puzzleId}/{isDaily}") {
        fun createRoute(puzzleId: Int, isDaily: Boolean = false) =
            "game/$puzzleId/$isDaily"
    }

    object Win : Screen("win/{puzzleId}/{time}/{errors}/{isDaily}") {
        fun createRoute(
            puzzleId: Int,
            time: Long,
            errors: Int,
            isDaily: Boolean = false
        ) = "win/$puzzleId/$time/$errors/$isDaily"
    }

    object Daily : Screen("daily")
    object Settings : Screen("settings")
}
