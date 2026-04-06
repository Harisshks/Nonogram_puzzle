package com.nonogram.puzzle.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nonogram.puzzle.data.db.AppDatabase
import com.nonogram.puzzle.data.model.PuzzleCategory
import com.nonogram.puzzle.data.preferences.UserPreferences
import com.nonogram.puzzle.data.progress.ProgressManager
import com.nonogram.puzzle.data.repository.PuzzleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TodaysGoalsUi(
    val anyLevelsDone: Int,
    val regularLevelsDone: Int,
    val hintsUsed: Int
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val prefs = UserPreferences(application)
    private val repository = PuzzleRepository(db.puzzleDao(), prefs)
    private val progressManager = ProgressManager(prefs)

    private val unlockedMaxByCategory = PuzzleCategory.entries.associateWith { cat ->
        progressManager.highestUnlockedLevelFlow(cat.name)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)
    }

    fun unlockedMaxLevel(category: PuzzleCategory): StateFlow<Int> =
        unlockedMaxByCategory.getValue(category)

    val completedCount: StateFlow<Int> = repository.getCompletedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val perfectCount: StateFlow<Int> = repository.getPerfectCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val streak: StateFlow<Int> = repository.streak
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalTimeSolved: StateFlow<Long> = repository.getTotalTimeSolved()
        .map { it ?: 0L }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    private val _completedPuzzleIds = MutableStateFlow<Set<Int>>(emptySet())
    val completedPuzzleIds: StateFlow<Set<Int>> = _completedPuzzleIds.asStateFlow()

    val completedStarsByPuzzleId: StateFlow<Map<Int, Int>> = repository.getAllProgress()
        .map { list ->
            list.filter { it.isCompleted }.associate { it.puzzleId to it.stars }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val todaysGoals: StateFlow<TodaysGoalsUi> = combine(
        repository.getAllProgress(),
        repository.dailyPuzzleId,
        repository.hintsUsedToday
    ) { progress, dailyId, hints ->
        val counts = repository.getCompletedLevelsToday(progress, dailyId)
        TodaysGoalsUi(
            anyLevelsDone = counts.anyLevels,
            regularLevelsDone = counts.regularLevels,
            hintsUsed = hints
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        TodaysGoalsUi(0, 0, 0)
    )

    val levelsSolvedToday: StateFlow<Int> = combine(
        repository.getAllProgress(),
        repository.dailyPuzzleId
    ) { progress, dailyId ->
        repository.getCompletedLevelsToday(progress, dailyId).anyLevels
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val hintsUsedToday: StateFlow<Int> = repository.hintsUsedToday
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        loadTodayGoals()
        viewModelScope.launch {
            progressManager.migrateFromLegacyCompletions(repository.getCompletedPuzzleIds())
        }
        viewModelScope.launch {
            repository.getAllProgress().collect { list ->
                _completedPuzzleIds.value =
                    list.filter { it.isCompleted }.map { it.puzzleId }.toSet()
            }
        }
    }

    fun loadTodayGoals() {
        viewModelScope.launch { refreshCompletedIds() }
    }

    fun recordLevelSolvedToday() {
        viewModelScope.launch { refreshCompletedIds() }
    }

    fun refreshCompletedIds() {
        viewModelScope.launch {
            _completedPuzzleIds.value = repository.getCompletedPuzzleIds()
        }
    }

    fun formatTotalTime(seconds: Long): String {
        val hours = seconds / 3600
        val mins = (seconds % 3600) / 60
        return if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
    }
}
