package com.nonogram.puzzle.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nonogram.puzzle.data.db.AppDatabase
import com.nonogram.puzzle.data.model.Cell
import com.nonogram.puzzle.data.model.CellState
import com.nonogram.puzzle.data.preferences.UserPreferences
import com.nonogram.puzzle.data.repository.PuzzleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TutorialViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = UserPreferences(application)
    private val repository = PuzzleRepository(
        AppDatabase.getDatabase(application).puzzleDao(),
        prefs
    )

    val tutorialCompleted: StateFlow<Boolean> =
        repository.tutorialCompleted
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                true
            )

    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    val totalSteps = 8

    val tutorialPuzzle: List<List<Int>> = listOf(
        listOf(0, 1, 0, 1, 0),
        listOf(0, 1, 0, 1, 0),
        listOf(0, 0, 0, 0, 0),
        listOf(1, 0, 0, 0, 1),
        listOf(0, 1, 1, 1, 0)
    )

    private val _tutorialGrid = MutableStateFlow(
        List(5) { r ->
            List(5) { c -> Cell(r, c) }
        }
    )
    val tutorialGrid: StateFlow<List<List<Cell>>> = _tutorialGrid.asStateFlow()

    private val _highlightedCells =
        MutableStateFlow<Set<Pair<Int, Int>>>(emptySet())
    val highlightedCells: StateFlow<Set<Pair<Int, Int>>> =
        _highlightedCells.asStateFlow()

    private val _stepTaskDone = MutableStateFlow(false)
    val stepTaskDone: StateFlow<Boolean> = _stepTaskDone.asStateFlow()

    init {
        updateHighlights()
    }

    fun nextStep() {
        _stepTaskDone.value = false
        if (_currentStep.value < totalSteps - 1) {
            _currentStep.value++
            updateHighlights()
        }
    }

    fun prevStep() {
        if (_currentStep.value > 0) {
            _currentStep.value--
            _stepTaskDone.value = false
            updateHighlights()
        }
    }

    private fun updateHighlights() {
        _highlightedCells.value = when (_currentStep.value) {
            4 -> setOf(0 to 1, 0 to 3)
            5 -> setOf(1 to 0, 1 to 2, 1 to 4)
            else -> emptySet()
        }
    }

    fun onTutorialCellTap(row: Int, col: Int) {
        val step = _currentStep.value
        val newGrid = _tutorialGrid.value
            .map { it.toMutableList() }.toMutableList()

        when (step) {
            4 -> {
                if (tutorialPuzzle[row][col] == 1) {
                    newGrid[row][col] = newGrid[row][col]
                        .copy(state = CellState.FILLED)
                    _tutorialGrid.value = newGrid
                    val filled = newGrid[0].count {
                        it.state == CellState.FILLED
                    }
                    if (filled >= 2) _stepTaskDone.value = true
                }
            }
            5 -> {
                if (tutorialPuzzle[row][col] == 0) {
                    newGrid[row][col] = newGrid[row][col]
                        .copy(state = CellState.MARKED)
                    _tutorialGrid.value = newGrid
                    val marked = newGrid.flatten().count {
                        it.state == CellState.MARKED
                    }
                    if (marked >= 1) _stepTaskDone.value = true
                }
            }
            6 -> {
                val current = newGrid[row][col].state
                newGrid[row][col] = when (current) {
                    CellState.MARKED -> newGrid[row][col].copy(state = CellState.EMPTY)
                    CellState.FILLED -> newGrid[row][col].copy(state = CellState.EMPTY)
                    CellState.EMPTY -> {
                        if (tutorialPuzzle[row][col] == 1) {
                            newGrid[row][col].copy(state = CellState.FILLED)
                        } else {
                            newGrid[row][col]
                        }
                    }
                }
                _tutorialGrid.value = newGrid
                val solved = newGrid.indices.all { r ->
                    newGrid[r].indices.all { c ->
                        (newGrid[r][c].state == CellState.FILLED) ==
                            (tutorialPuzzle[r][c] == 1)
                    }
                }
                if (solved) _stepTaskDone.value = true
            }
        }
    }

    fun onTutorialCellLongPress(row: Int, col: Int) {
        val step = _currentStep.value
        if (step != 5 && step != 6) return
        val newGrid = _tutorialGrid.value
            .map { it.toMutableList() }.toMutableList()
        if (tutorialPuzzle[row][col] != 0) return

        val current = newGrid[row][col].state
        newGrid[row][col] = newGrid[row][col].copy(
            state = if (current == CellState.MARKED) {
                CellState.EMPTY
            } else {
                CellState.MARKED
            }
        )
        _tutorialGrid.value = newGrid

        if (step == 5) {
            val marked = newGrid.flatten().count { it.state == CellState.MARKED }
            if (marked >= 1) _stepTaskDone.value = true
        } else if (step == 6) {
            val solved = newGrid.indices.all { r ->
                newGrid[r].indices.all { c ->
                    (newGrid[r][c].state == CellState.FILLED) ==
                        (tutorialPuzzle[r][c] == 1)
                }
            }
            if (solved) _stepTaskDone.value = true
        }
    }

    fun completeTutorial() {
        viewModelScope.launch {
            repository.completeTutorial()
        }
    }

    fun skipTutorial() {
        viewModelScope.launch {
            repository.skipTutorial()
        }
    }

    fun restartSession() {
        _currentStep.value = 0
        _tutorialGrid.value = List(5) { r ->
            List(5) { c -> Cell(r, c) }
        }
        _stepTaskDone.value = false
        updateHighlights()
    }
}
