package com.nonogram.puzzle.data.progress

import com.nonogram.puzzle.data.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Facade for monotonic per-category level unlock progress.
 * All persistence goes through [UserPreferences] so only one DataStore (`user_prefs`) is active.
 *
 * Stored keys: `progress_<category>` = highest unlocked level number (inclusive). Default: 1.
 */
class ProgressManager(private val prefs: UserPreferences) {

    fun highestUnlockedLevelFlow(categoryName: String): Flow<Int> =
        prefs.categoryHighestUnlockedLevelFlow(categoryName)

    suspend fun getProgress(category: String): Int =
        prefs.getCategoryHighestUnlockedLevel(category)

    suspend fun saveProgress(category: String, newLevel: Int) {
        prefs.saveCategoryProgressIfGreater(category, newLevel)
    }

    suspend fun onLevelCompleted(category: String, level: Int) {
        prefs.onCategoryLevelCompleted(category, level)
    }

    suspend fun migrateFromLegacyCompletions(completedPuzzleIds: Set<Int>) {
        prefs.migrateCategoryProgressFromLegacyCompletions(completedPuzzleIds)
    }

    suspend fun clearAllCategoryProgress() {
        prefs.clearAllCategoryProgressKeys()
    }
}
