package com.nonogram.puzzle.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nonogram.puzzle.data.db.AppDatabase
import com.nonogram.puzzle.data.preferences.UserPreferences
import com.nonogram.puzzle.data.repository.PuzzleRepository
import com.nonogram.puzzle.utils.SoundManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val prefs = UserPreferences(application)
    private val repository = PuzzleRepository(db.puzzleDao(), prefs)

    val soundEnabled: StateFlow<Boolean> = repository.soundEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val vibrationEnabled: StateFlow<Boolean> = repository.vibrationEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val errorHighlight: StateFlow<Boolean> = repository.errorHighlight
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val musicEnabled: StateFlow<Boolean> = repository.musicEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val completedCount: StateFlow<Int> = repository.getCompletedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val perfectCount: StateFlow<Int> = repository.getPerfectCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val darkMode: StateFlow<Boolean> = repository.darkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setSoundEnabled(enabled)
            SoundManager.getInstance(getApplication()).setSoundEnabled(enabled)
        }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setVibrationEnabled(enabled) }
    }

    fun setErrorHighlight(enabled: Boolean) {
        viewModelScope.launch { repository.setErrorHighlight(enabled) }
    }

    fun setMusicEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setMusicEnabled(enabled) }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { repository.setDarkMode(enabled) }
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            repository.clearAllProgress()
        }
    }

    fun resetTutorial() {
        viewModelScope.launch {
            repository.resetTutorial()
        }
    }
}
