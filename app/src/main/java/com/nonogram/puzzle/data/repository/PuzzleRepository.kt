package com.nonogram.puzzle.data.repository

import com.nonogram.puzzle.data.db.PuzzleDao
import com.nonogram.puzzle.data.model.PuzzleProgress
import com.nonogram.puzzle.data.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId

class PuzzleRepository(
    private val dao: PuzzleDao,
    private val userPreferences: UserPreferences
) {

    suspend fun saveProgress(progress: PuzzleProgress) = dao.saveProgress(progress)

    suspend fun getProgress(puzzleId: Int): PuzzleProgress? = dao.getProgress(puzzleId)

    suspend fun deleteProgress(puzzleId: Int) = dao.deleteProgress(puzzleId)

    suspend fun clearAllProgress() = dao.clearAllProgress()

    fun getCompletedCount(): Flow<Int> = dao.getCompletedCount()

    fun getCompletedCountByCategory(category: String): Flow<Int> =
        dao.getCompletedCountByCategory(category)

    fun getAllProgress(): Flow<List<PuzzleProgress>> = dao.getAllProgress()

    fun getPerfectCount(): Flow<Int> = dao.getPerfectCount()

    fun getTotalTimeSolved(): Flow<Long?> = dao.getTotalTime()

    suspend fun getCompletedPuzzles(): List<PuzzleProgress> = dao.getAllCompleted()

    suspend fun getCompletedPuzzleIds(): Set<Int> =
        dao.getAllCompleted().map { it.puzzleId }.toSet()

    suspend fun getBestTimeForPuzzle(id: Int): Long? = dao.getBestTime(id)

    val streak: Flow<Int> = userPreferences.streak
    val lastPlayedDate: Flow<String> = userPreferences.lastPlayedDate
    val dailyCompletedDate: Flow<String> = userPreferences.dailyCompletedDate
    val soundEnabled: Flow<Boolean> = userPreferences.soundEnabled
    val vibrationEnabled: Flow<Boolean> = userPreferences.vibrationEnabled
    val errorHighlight: Flow<Boolean> = userPreferences.errorHighlight
    val musicEnabled: Flow<Boolean> = userPreferences.musicEnabled
    val totalPuzzlesCompleted: Flow<Int> = userPreferences.totalPuzzlesCompleted
    val dailyPuzzleId: Flow<Int> = userPreferences.dailyPuzzleId

    val hintsUsedToday: Flow<Int> = userPreferences.hintsUsedToday

    val darkMode: Flow<Boolean> = userPreferences.darkMode

    val tutorialCompleted: Flow<Boolean> = userPreferences.tutorialCompleted

    suspend fun incrementHintsUsedToday() = userPreferences.incrementHintsUsedToday()

    suspend fun setDarkMode(enabled: Boolean) = userPreferences.setDarkMode(enabled)

    fun getCompletedLevelsToday(progress: List<PuzzleProgress>, dailyPuzzleId: Int): CompletedTodayCounts {
        val z = ZoneId.systemDefault()
        val start = LocalDate.now().atStartOfDay(z).toInstant().toEpochMilli()
        val end = start + 86400000L
        val done = progress.filter { p ->
            p.isCompleted && p.lastUpdated >= start && p.lastUpdated < end
        }
        return CompletedTodayCounts(
            anyLevels = done.size,
            regularLevels = done.count { it.puzzleId != dailyPuzzleId }
        )
    }

    data class CompletedTodayCounts(val anyLevels: Int, val regularLevels: Int)

    suspend fun updateStreakOnPlay() {
        val today = LocalDate.now().toString()
        val lastDate = userPreferences.lastPlayedDate.first()
        if (lastDate == today) return
        val yesterday = LocalDate.now().minusDays(1).toString()
        val currentStreak = userPreferences.streak.first()
        val newStreak = when (lastDate) {
            yesterday -> currentStreak + 1
            else -> 1
        }
        userPreferences.updateStreak(newStreak)
        userPreferences.updateLastPlayedDate(today)
    }

    suspend fun markDailyCompleted() {
        val today = LocalDate.now().toString()
        userPreferences.updateDailyCompletedDate(today)
        updateStreakOnPlay()
    }

    fun isDailyCompletedToday(): Flow<Boolean> =
        userPreferences.dailyCompletedDate.map { date ->
            date == LocalDate.now().toString()
        }

    fun getTodaysDailyPuzzleId(allPuzzleIds: List<Int>): Int {
        if (allPuzzleIds.isEmpty()) return 1001
        val day = LocalDate.now().dayOfYear
        return allPuzzleIds[day % allPuzzleIds.size]
    }

    suspend fun setSoundEnabled(enabled: Boolean) =
        userPreferences.setSoundEnabled(enabled)

    suspend fun setVibrationEnabled(enabled: Boolean) =
        userPreferences.setVibrationEnabled(enabled)

    suspend fun setErrorHighlight(enabled: Boolean) =
        userPreferences.setErrorHighlight(enabled)

    suspend fun setMusicEnabled(enabled: Boolean) =
        userPreferences.setMusicEnabled(enabled)

    suspend fun incrementTotalCompleted() =
        userPreferences.incrementTotalCompleted()

    suspend fun completeTutorial() = userPreferences.setTutorialCompleted()

    suspend fun skipTutorial() = userPreferences.setTutorialSkipped()

    suspend fun resetTutorial() = userPreferences.resetTutorial()
}
