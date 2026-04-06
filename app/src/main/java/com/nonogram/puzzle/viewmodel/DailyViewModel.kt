package com.nonogram.puzzle.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nonogram.puzzle.data.db.AppDatabase
import com.nonogram.puzzle.data.model.Puzzle
import com.nonogram.puzzle.data.model.PuzzleProgress
import com.nonogram.puzzle.data.preferences.UserPreferences
import com.nonogram.puzzle.data.repository.PuzzleRepository
import com.nonogram.puzzle.utils.PuzzleData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class DailyViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val prefs = UserPreferences(application)
    private val repository = PuzzleRepository(db.puzzleDao(), prefs)

    val dailyPuzzleId: Int =
        repository.getTodaysDailyPuzzleId(PuzzleData.all.map { it.id })

    val dailyPuzzle: Puzzle =
        PuzzleData.getById(dailyPuzzleId)
            ?: PuzzleData.all.first()

    val isDailyCompleted: StateFlow<Boolean> = repository.isDailyCompletedToday()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val streak: StateFlow<Int> = repository.streak
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _last7Days = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList())
    val last7Days: StateFlow<List<Pair<String, Boolean>>> = _last7Days.asStateFlow()

    private val _timeUntilNext = MutableStateFlow(Triple(0, 0, 0))
    val timeUntilNext: StateFlow<Triple<Int, Int, Int>> = _timeUntilNext.asStateFlow()

    private val _pastDailies = MutableStateFlow<List<PuzzleProgress>>(emptyList())
    val pastDailies: StateFlow<List<PuzzleProgress>> = _pastDailies.asStateFlow()

    init {
        startCountdown()
        loadPastDailies()
        viewModelScope.launch {
            buildLast7Days()
        }
    }

    private fun startCountdown() {
        viewModelScope.launch {
            while (true) {
                val now = java.time.LocalDateTime.now()
                val midnight = now.toLocalDate().plusDays(1).atStartOfDay()
                val secondsLeft = java.time.Duration.between(now, midnight).seconds
                _timeUntilNext.value = Triple(
                    (secondsLeft / 3600).toInt(),
                    ((secondsLeft % 3600) / 60).toInt(),
                    (secondsLeft % 60).toInt()
                )
                delay(1000L)
            }
        }
    }

    private fun loadPastDailies() {
        viewModelScope.launch {
            _pastDailies.value = repository.getCompletedPuzzles()
                .sortedByDescending { it.lastUpdated }
                .take(5)
        }
    }

    private suspend fun buildLast7Days() {
        val today = LocalDate.now()
        val streakVal = repository.streak.first()
        val days = (6 downTo 0).map { offset ->
            val date = today.minusDays(offset.toLong())
            val label = date.dayOfWeek.name.take(1)
            val wasPlayed = offset < streakVal.coerceAtMost(7)
            Pair(label, wasPlayed)
        }
        _last7Days.value = days
    }

    fun markDailyCompleted() {
        viewModelScope.launch {
            repository.markDailyCompleted()
            buildLast7Days()
            loadPastDailies()
        }
    }
}
