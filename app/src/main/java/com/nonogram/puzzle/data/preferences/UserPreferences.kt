package com.nonogram.puzzle.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val KEY_STREAK = intPreferencesKey("streak")
        val KEY_LAST_PLAYED_DATE = stringPreferencesKey("last_played_date")
        val KEY_DAILY_COMPLETED_DATE = stringPreferencesKey("daily_completed_date")
        val KEY_SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val KEY_VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val KEY_ERROR_HIGHLIGHT = booleanPreferencesKey("error_highlight")
        val KEY_TOTAL_PUZZLES_COMPLETED = intPreferencesKey("total_puzzles_completed")
        val KEY_HINTS_USED_TODAY = intPreferencesKey("hints_used_today")
        val KEY_HINTS_DATE = stringPreferencesKey("hints_date")
        val KEY_DAILY_PUZZLE_ID = intPreferencesKey("daily_puzzle_id")
        val KEY_MUSIC_ENABLED = booleanPreferencesKey("music_enabled")
        val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
        val KEY_MARK_TIP_SHOWN = booleanPreferencesKey("mark_tip_shown")
        val KEY_TUTORIAL_COMPLETED = booleanPreferencesKey("tutorial_completed")
        val KEY_TUTORIAL_SKIPPED = booleanPreferencesKey("tutorial_skipped")
    }

    val streak: Flow<Int> = context.userDataStore.data
        .map { it[KEY_STREAK] ?: 0 }

    val lastPlayedDate: Flow<String> = context.userDataStore.data
        .map { it[KEY_LAST_PLAYED_DATE] ?: "" }

    val dailyCompletedDate: Flow<String> = context.userDataStore.data
        .map { it[KEY_DAILY_COMPLETED_DATE] ?: "" }

    val soundEnabled: Flow<Boolean> = context.userDataStore.data
        .map { it[KEY_SOUND_ENABLED] ?: true }

    val vibrationEnabled: Flow<Boolean> = context.userDataStore.data
        .map { it[KEY_VIBRATION_ENABLED] ?: true }

    val errorHighlight: Flow<Boolean> = context.userDataStore.data
        .map { it[KEY_ERROR_HIGHLIGHT] ?: true }

    val musicEnabled: Flow<Boolean> = context.userDataStore.data
        .map { it[KEY_MUSIC_ENABLED] ?: false }

    val darkMode: Flow<Boolean> = context.userDataStore.data
        .map { it[KEY_DARK_MODE] ?: false }

    val markTipShown: Flow<Boolean> = context.userDataStore.data
        .map { it[KEY_MARK_TIP_SHOWN] ?: false }

    val tutorialCompleted: Flow<Boolean> = context.userDataStore.data
        .map { it[KEY_TUTORIAL_COMPLETED] ?: false }

    val tutorialSkipped: Flow<Boolean> = context.userDataStore.data
        .map { it[KEY_TUTORIAL_SKIPPED] ?: false }

    val totalPuzzlesCompleted: Flow<Int> = context.userDataStore.data
        .map { it[KEY_TOTAL_PUZZLES_COMPLETED] ?: 0 }

    val dailyPuzzleId: Flow<Int> = context.userDataStore.data
        .map { it[KEY_DAILY_PUZZLE_ID] ?: 1 }

    val hintsUsedToday: Flow<Int> = context.userDataStore.data.map { prefs ->
        val today = LocalDate.now().toString()
        if (prefs[KEY_HINTS_DATE] != today) 0 else (prefs[KEY_HINTS_USED_TODAY] ?: 0)
    }

    suspend fun updateStreak(newStreak: Int) {
        context.userDataStore.edit { it[KEY_STREAK] = newStreak }
    }

    suspend fun updateLastPlayedDate(date: String) {
        context.userDataStore.edit { it[KEY_LAST_PLAYED_DATE] = date }
    }

    suspend fun updateDailyCompletedDate(date: String) {
        context.userDataStore.edit { it[KEY_DAILY_COMPLETED_DATE] = date }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.userDataStore.edit { it[KEY_SOUND_ENABLED] = enabled }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.userDataStore.edit { it[KEY_VIBRATION_ENABLED] = enabled }
    }

    suspend fun setErrorHighlight(enabled: Boolean) {
        context.userDataStore.edit { it[KEY_ERROR_HIGHLIGHT] = enabled }
    }

    suspend fun setMusicEnabled(enabled: Boolean) {
        context.userDataStore.edit { it[KEY_MUSIC_ENABLED] = enabled }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.userDataStore.edit { it[KEY_DARK_MODE] = enabled }
    }

    suspend fun setMarkTipShown() {
        context.userDataStore.edit { it[KEY_MARK_TIP_SHOWN] = true }
    }

    suspend fun setTutorialCompleted() {
        context.userDataStore.edit {
            it[KEY_TUTORIAL_COMPLETED] = true
        }
    }

    suspend fun setTutorialSkipped() {
        context.userDataStore.edit {
            it[KEY_TUTORIAL_SKIPPED] = true
            it[KEY_TUTORIAL_COMPLETED] = true
        }
    }

    suspend fun resetTutorial() {
        context.userDataStore.edit {
            it[KEY_TUTORIAL_COMPLETED] = false
            it[KEY_TUTORIAL_SKIPPED] = false
        }
    }

    suspend fun incrementTotalCompleted() {
        context.userDataStore.edit {
            it[KEY_TOTAL_PUZZLES_COMPLETED] = (it[KEY_TOTAL_PUZZLES_COMPLETED] ?: 0) + 1
        }
    }

    suspend fun updateDailyPuzzleId(id: Int) {
        context.userDataStore.edit { it[KEY_DAILY_PUZZLE_ID] = id }
    }

    suspend fun incrementHintsUsedToday() {
        val today = LocalDate.now().toString()
        context.userDataStore.edit { prefs ->
            if (prefs[KEY_HINTS_DATE] != today) {
                prefs[KEY_HINTS_DATE] = today
                prefs[KEY_HINTS_USED_TODAY] = 1
            } else {
                prefs[KEY_HINTS_USED_TODAY] = (prefs[KEY_HINTS_USED_TODAY] ?: 0) + 1
            }
        }
    }
}
