package com.nonogram.puzzle.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nonogram.puzzle.data.db.AppDatabase
import com.nonogram.puzzle.data.model.Cell
import com.nonogram.puzzle.data.model.CellState
import com.nonogram.puzzle.data.model.Puzzle
import com.nonogram.puzzle.data.model.PuzzleProgress
import com.nonogram.puzzle.data.preferences.UserPreferences
import com.nonogram.puzzle.data.progress.ProgressManager
import com.nonogram.puzzle.data.repository.PuzzleRepository
import com.nonogram.puzzle.utils.PuzzleData
import com.nonogram.puzzle.utils.PuzzleValidator
import com.nonogram.puzzle.utils.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val db = AppDatabase.getDatabase(application)
    private val prefs = UserPreferences(application)
    private val repository = PuzzleRepository(db.puzzleDao(), prefs)
    private val progressManager = ProgressManager(prefs)

    private val _puzzle = MutableStateFlow<Puzzle?>(null)
    val puzzle: StateFlow<Puzzle?> = _puzzle.asStateFlow()

    private val _grid = MutableStateFlow<List<List<Cell>>>(emptyList())
    val grid: StateFlow<List<List<Cell>>> = _grid.asStateFlow()

    private val _isCompleted = MutableStateFlow(false)
    val isCompleted: StateFlow<Boolean> = _isCompleted.asStateFlow()

    private val _errors = MutableStateFlow(0)
    val errors: StateFlow<Int> = _errors.asStateFlow()

    private val _hintsRemaining = MutableStateFlow(3)
    val hintsRemaining: StateFlow<Int> = _hintsRemaining.asStateFlow()

    private val _timer = MutableStateFlow(0L)
    val timer: StateFlow<Long> = _timer.asStateFlow()

    private val _stars = MutableStateFlow(0)
    val stars: StateFlow<Int> = _stars.asStateFlow()

    private val _showErrorFlash = MutableStateFlow(false)
    val showErrorFlash: StateFlow<Boolean> = _showErrorFlash.asStateFlow()

    private val _outOfLives = MutableStateFlow(false)
    val outOfLives: StateFlow<Boolean> = _outOfLives.asStateFlow()

    private val _heartsLeft = MutableStateFlow(3)
    val heartsLeft: StateFlow<Int> = _heartsLeft.asStateFlow()

    val errorHighlight: StateFlow<Boolean> = repository.errorHighlight
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    private var timerJob: Job? = null
    private var isDragging = false
    private var dragFillState: CellState = CellState.FILLED

    private var isDailySession = false
    private var rowDinged = BooleanArray(0)
    private var colDinged = BooleanArray(0)

    /**
     * When true, the player opened a puzzle that was already completed (replay).
     * We must not write partial [saveProgress] to Room or it replaces the completed row
     * and the level select screen loses completion / "next" state.
     */
    private var replayingCompletedPuzzle = false

    private val gson = Gson()

    fun loadPuzzle(puzzleId: Int, isDaily: Boolean = false) {
        timerJob?.cancel()
        val puzzle = PuzzleData.getById(puzzleId) ?: return
        isDailySession = isDaily
        _puzzle.value = puzzle
        rowDinged = BooleanArray(puzzle.gridSize) { false }
        colDinged = BooleanArray(puzzle.gridSize) { false }

        viewModelScope.launch {
            SoundManager.getInstance(context).setSoundEnabled(repository.soundEnabled.first())
            _errors.value = 0
            _heartsLeft.value = 3
            _hintsRemaining.value = 3
            _isCompleted.value = false
            _stars.value = 0
            _outOfLives.value = false
            val saved = repository.getProgress(puzzleId)
            replayingCompletedPuzzle = saved?.isCompleted == true
            if (saved != null && !saved.isCompleted) {
                _grid.value = restoreGrid(puzzle.gridSize, saved.cellStates)
                _timer.value = saved.timeSpent
                _errors.value = saved.errors
                _hintsRemaining.value = (3 - saved.hintsUsed).coerceAtLeast(0)
                _heartsLeft.value = (3 - _errors.value).coerceAtLeast(0)
            } else {
                _grid.value = List(puzzle.gridSize) { r ->
                    List(puzzle.gridSize) { c -> Cell(r, c) }
                }
                _timer.value = 0L
            }
            _outOfLives.value = _errors.value >= MAX_LIVES && !_isCompleted.value
            for (r in 0 until puzzle.gridSize) {
                if (isRowSatisfied(r)) rowDinged[r] = true
            }
            for (c in 0 until puzzle.gridSize) {
                if (isColSatisfied(c)) colDinged[c] = true
            }
            if (_outOfLives.value) {
                timerJob?.cancel()
            } else {
                startTimer()
            }
            delay(300L)
            SoundManager.getInstance(context).playLevelStart()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (!_isCompleted.value) {
                delay(1000L)
                _timer.value++
                if (_timer.value % 10L == 0L) {
                    saveProgress()
                }
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
    }

    fun startTimerIfNotComplete() {
        if (_isCompleted.value || _outOfLives.value) return
        startTimer()
    }

    /** Same as [addLifeFromRewardedAd] — restores one life after rewarded video. */
    fun addHeart() = addLifeFromRewardedAd()

    fun onCellTap(row: Int, col: Int) {
        if (_outOfLives.value || _isCompleted.value) return
        val current = _grid.value[row][col].state
        val newState = when (current) {
            CellState.EMPTY -> CellState.FILLED
            CellState.FILLED -> CellState.EMPTY
            CellState.MARKED -> CellState.EMPTY
        }
        applyCell(row, col, newState)
    }

    fun onCellLongPress(row: Int, col: Int) {
        if (_outOfLives.value || _isCompleted.value) return
        val current = _grid.value[row][col].state
        applyCell(
            row, col,
            if (current == CellState.MARKED) CellState.EMPTY else CellState.MARKED
        )
    }

    fun onDragStart(row: Int, col: Int) {
        if (_outOfLives.value || _isCompleted.value) return
        isDragging = true
        val current = _grid.value[row][col].state
        dragFillState = if (current == CellState.FILLED) CellState.EMPTY else CellState.FILLED
        applyCell(row, col, dragFillState)
    }

    fun onDragEnter(row: Int, col: Int) {
        if (!isDragging || _outOfLives.value) return
        applyCell(row, col, dragFillState)
    }

    fun onDragEnd() {
        isDragging = false
    }

    private fun applyCell(row: Int, col: Int, newState: CellState, fromHint: Boolean = false) {
        if (_outOfLives.value) return
        val puzzle = _puzzle.value ?: return
        val newGrid = _grid.value.map { it.toMutableList() }.toMutableList()
        newGrid[row][col] = newGrid[row][col].copy(state = newState)
        _grid.value = newGrid

        if (newState == CellState.FILLED && puzzle.solution[row][col] == 0) {
            _errors.value++
            _heartsLeft.value = (3 - _errors.value).coerceAtLeast(0)
            viewModelScope.launch {
                _showErrorFlash.value = true
                delay(400L)
                _showErrorFlash.value = false
            }
            SoundManager.getInstance(context).playWrongCell()
            if (_errors.value >= MAX_LIVES) {
                _outOfLives.value = true
                stopTimer()
                SoundManager.getInstance(context).playGameOver()
                viewModelScope.launch { saveProgress() }
            } else {
                SoundManager.getInstance(context).playHeartBreak()
            }
        } else if (newState == CellState.FILLED && !fromHint) {
            SoundManager.getInstance(context).playCellFill()
        } else if (newState == CellState.MARKED) {
            SoundManager.getInstance(context).playCellMark()
        }

        maybePlayLineSatisfied(row, col)
        checkIfSolved()
    }

    private fun maybePlayLineSatisfied(row: Int, col: Int) {
        val puzzle = _puzzle.value ?: return
        if (!rowDinged[row] && isRowSatisfied(row)) {
            rowDinged[row] = true
            SoundManager.getInstance(context).playRowSatisfied()
        }
        if (!colDinged[col] && isColSatisfied(col)) {
            colDinged[col] = true
            SoundManager.getInstance(context).playRowSatisfied()
        }
    }

    fun useHint() {
        if (_outOfLives.value || _hintsRemaining.value <= 0) return
        val puzzle = _puzzle.value ?: return
        for (r in _grid.value.indices) {
            for (c in _grid.value[r].indices) {
                if (puzzle.solution[r][c] == 1 &&
                    _grid.value[r][c].state != CellState.FILLED
                ) {
                    applyCell(r, c, CellState.FILLED, fromHint = true)
                    _hintsRemaining.value--
                    viewModelScope.launch {
                        repository.incrementHintsUsedToday()
                        saveProgress()
                    }
                    SoundManager.getInstance(context).playHint()
                    return
                }
            }
        }
    }

    fun addHintsFromAd() {
        _hintsRemaining.value += 3
    }

    /** Restores one life after watching a rewarded ad (reduces error count by one). */
    fun addLifeFromRewardedAd() {
        if (!_outOfLives.value) return
        _errors.value = (_errors.value - 1).coerceAtLeast(0)
        _heartsLeft.value = (3 - _errors.value).coerceAtLeast(0)
        _outOfLives.value = false
        startTimer()
        viewModelScope.launch { saveProgress() }
    }

    fun resetPuzzle() {
        val puzzle = _puzzle.value ?: return
        replayingCompletedPuzzle = false
        viewModelScope.launch {
            repository.deleteProgress(puzzle.id)
        }
        rowDinged = BooleanArray(puzzle.gridSize) { false }
        colDinged = BooleanArray(puzzle.gridSize) { false }
        _grid.value = List(puzzle.gridSize) { r ->
            List(puzzle.gridSize) { c -> Cell(r, c) }
        }
        _errors.value = 0
        _heartsLeft.value = 3
        _outOfLives.value = false
        _isCompleted.value = false
        _timer.value = 0L
        _hintsRemaining.value = 3
        startTimer()
        SoundManager.getInstance(context).playLevelStart()
    }

    private fun checkIfSolved() {
        val puzzle = _puzzle.value ?: return
        val solved = _grid.value.indices.all { r ->
            _grid.value[r].indices.all { c ->
                (_grid.value[r][c].state == CellState.FILLED) ==
                    (puzzle.solution[r][c] == 1)
            }
        }
        if (solved) {
            _isCompleted.value = true
            timerJob?.cancel()
            _stars.value = calculateStars()
            val sm = SoundManager.getInstance(context)
            if (isDailySession) {
                sm.playDailyComplete()
            } else {
                sm.playPuzzleWin()
                if (_stars.value == 3) {
                    viewModelScope.launch {
                        delay(600L)
                        SoundManager.getInstance(context).playAllStars()
                    }
                }
            }
            viewModelScope.launch {
                saveCompletedProgress()
                repository.incrementTotalCompleted()
                repository.updateStreakOnPlay()
                if (!isDailySession) {
                    progressManager.onLevelCompleted(
                        puzzle.category.name,
                        puzzle.levelNumber
                    )
                }
            }
        }
    }

    private fun calculateStars(): Int = when {
        _errors.value == 0 -> 3
        _errors.value <= 3 -> 2
        else -> 1
    }

    private suspend fun saveProgress() {
        val puzzle = _puzzle.value ?: return
        if (_isCompleted.value) return
        if (replayingCompletedPuzzle) return
        repository.saveProgress(
            PuzzleProgress(
                puzzleId = puzzle.id,
                category = puzzle.category.name,
                cellStates = serializeGrid(_grid.value),
                timeSpent = _timer.value,
                isCompleted = false,
                stars = 0,
                hintsUsed = 3 - _hintsRemaining.value,
                errors = _errors.value
            )
        )
    }

    private suspend fun saveCompletedProgress() {
        val puzzle = _puzzle.value ?: return
        repository.saveProgress(
            PuzzleProgress(
                puzzleId = puzzle.id,
                category = puzzle.category.name,
                cellStates = serializeGrid(_grid.value),
                timeSpent = _timer.value,
                isCompleted = true,
                stars = _stars.value,
                hintsUsed = 3 - _hintsRemaining.value,
                errors = _errors.value
            )
        )
    }

    private fun serializeGrid(grid: List<List<Cell>>): String {
        val states = grid.map { row ->
            row.map { cell ->
                when (cell.state) {
                    CellState.EMPTY -> 0
                    CellState.FILLED -> 1
                    CellState.MARKED -> 2
                }
            }
        }
        return gson.toJson(states)
    }

    private fun restoreGrid(size: Int, json: String): List<List<Cell>> {
        return try {
            val type = object : TypeToken<List<List<Int>>>() {}.type
            val states: List<List<Int>> = gson.fromJson(json, type)
            List(size) { r ->
                List(size) { c ->
                    val stateInt = states.getOrNull(r)?.getOrNull(c) ?: 0
                    Cell(
                        r, c,
                        when (stateInt) {
                            1 -> CellState.FILLED
                            2 -> CellState.MARKED
                            else -> CellState.EMPTY
                        }
                    )
                }
            }
        } catch (_: Exception) {
            List(size) { r -> List(size) { c -> Cell(r, c) } }
        }
    }

    fun isRowSatisfied(row: Int): Boolean {
        val puzzle = _puzzle.value ?: return false
        val line = _grid.value[row].map {
            if (it.state == CellState.FILLED) 1 else 0
        }
        return PuzzleValidator.calculateClues(line) == puzzle.rowClues[row]
    }

    fun isColSatisfied(col: Int): Boolean {
        val puzzle = _puzzle.value ?: return false
        val size = puzzle.gridSize
        val line = (0 until size).map {
            if (_grid.value[it][col].state == CellState.FILLED) 1 else 0
        }
        return PuzzleValidator.calculateClues(line) == puzzle.colClues[col]
    }

    fun getCompletionPercent(): Float {
        val puzzle = _puzzle.value ?: return 0f
        val total = puzzle.gridSize * puzzle.gridSize
        val filled = _grid.value.sumOf { row -> row.count { it.state == CellState.FILLED } }
        return filled.toFloat() / total.toFloat()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        runBlocking {
            saveProgress()
        }
    }

    private companion object {
        const val MAX_LIVES = 3
    }
}
