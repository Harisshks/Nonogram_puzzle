package com.nonogram.puzzle.utils

import com.nonogram.puzzle.data.model.Cell
import com.nonogram.puzzle.data.model.CellState
import com.nonogram.puzzle.data.model.Puzzle

object PuzzleValidator {
    fun calculateClues(line: List<Int>): List<Int> {
        val clues = mutableListOf<Int>()
        var count = 0
        line.forEach { cell -> if (cell == 1) count++ else if (count > 0) { clues.add(count); count = 0 } }
        if (count > 0) clues.add(count)
        return clues.ifEmpty { listOf(0) }
    }

    fun isSolved(grid: List<List<Cell>>, puzzle: Puzzle): Boolean {
        val size = puzzle.gridSize
        for (r in 0 until size) {
            val rowLine = grid[r].map { if (it.state == CellState.FILLED) 1 else 0 }
            if (calculateClues(rowLine) != puzzle.rowClues[r]) return false
        }
        for (c in 0 until size) {
            val colLine = (0 until size).map { r -> if (grid[r][c].state == CellState.FILLED) 1 else 0 }
            if (calculateClues(colLine) != puzzle.colClues[c]) return false
        }
        return true
    }
}
