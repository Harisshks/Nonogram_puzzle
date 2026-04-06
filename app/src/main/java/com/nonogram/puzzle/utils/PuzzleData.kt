package com.nonogram.puzzle.utils

import com.nonogram.puzzle.data.model.Difficulty
import com.nonogram.puzzle.data.model.Puzzle
import com.nonogram.puzzle.data.model.PuzzleCategory

object PuzzleData {

    private fun line(line: List<Int>): List<Int> {
        val c = mutableListOf<Int>()
        var n = 0
        line.forEach {
            if (it == 1) n++
            else if (n > 0) {
                c.add(n)
                n = 0
            }
        }
        if (n > 0) c.add(n)
        return if (c.isEmpty()) listOf(0) else c
    }

    private fun build(
        id: Int,
        level: Int,
        title: String,
        category: PuzzleCategory,
        difficulty: Difficulty,
        solution: List<List<Int>>
    ): Puzzle {
        val size = solution.size
        return Puzzle(
            id = id,
            levelNumber = level,
            title = title,
            category = category,
            difficulty = difficulty,
            gridSize = size,
            solution = solution,
            rowClues = solution.map { line(it) },
            colClues = List(size) { c ->
                line(List(size) { r -> solution[r][c] })
            }
        )
    }

    private fun difficultyAndSizeForLevel(level: Int): Pair<Difficulty, Int> {
        val diff = when {
            level <= 20 -> Difficulty.EASY
            level <= 40 -> Difficulty.MEDIUM
            else -> Difficulty.HARD
        }
        val size = when {
            level <= 20 -> 5
            level <= 40 -> 7
            else -> 10
        }
        return diff to size
    }

    private fun generatedSolution(size: Int, seed: Int): List<List<Int>> {
        val grid = List(size) { r ->
            List(size) { c ->
                val h = (seed * 1315423911 + r * 224682251 + c * 79365049) xor (r * c * 17)
                if (kotlin.math.abs(h) % 5 < 2) 1 else 0
            }
        }.map { it.toMutableList() }.toMutableList()
        if (grid.all { row -> row.all { it == 0 } }) {
            grid[size / 2][size / 2] = 1
        }
        return grid
    }

    private fun fillerPuzzle(id: Int, category: PuzzleCategory): Puzzle {
        val base = when (category) {
            PuzzleCategory.ANIMALS -> 1000
            PuzzleCategory.FOOD -> 2000
            PuzzleCategory.SYMBOLS -> 3000
            PuzzleCategory.OBJECTS -> 4000
            PuzzleCategory.NATURE -> 5000
        }
        val level = id - base
        val (diff, size) = difficultyAndSizeForLevel(level)
        val sol = generatedSolution(size, id)
        return build(
            id, level,
            "${category.displayName} $level",
            category, diff, sol
        )
    }

    private fun mergeCategory(
        explicit: List<Puzzle>,
        range: IntRange,
        category: PuzzleCategory
    ): List<Puzzle> {
        val byId = explicit.associateBy { it.id }
        return range.map { id -> byId[id] ?: fillerPuzzle(id, category) }
    }

    // ── Explicit puzzles (manual); gaps use fillerPuzzle ─────────────────

    private val animalsExplicit: List<Puzzle> = PuzzleDataCatalog.animals
    private val foodExplicit: List<Puzzle> = PuzzleDataCatalog.food
    private val symbolsExplicit: List<Puzzle> = PuzzleDataCatalog.symbols
    private val objectsExplicit: List<Puzzle> = PuzzleDataCatalog.objects
    private val natureExplicit: List<Puzzle> = PuzzleDataCatalog.nature

    val animals: List<Puzzle> = mergeCategory(animalsExplicit, 1001..1050, PuzzleCategory.ANIMALS)
    val food: List<Puzzle> = mergeCategory(foodExplicit, 2001..2050, PuzzleCategory.FOOD)
    val symbols: List<Puzzle> = mergeCategory(symbolsExplicit, 3001..3050, PuzzleCategory.SYMBOLS)
    val objects: List<Puzzle> = mergeCategory(objectsExplicit, 4001..4050, PuzzleCategory.OBJECTS)
    val nature: List<Puzzle> = mergeCategory(natureExplicit, 5001..5050, PuzzleCategory.NATURE)

    val all: List<Puzzle> = animals + food + symbols + objects + nature

    fun getByCategory(cat: PuzzleCategory): List<Puzzle> = when (cat) {
        PuzzleCategory.ANIMALS -> animals
        PuzzleCategory.FOOD -> food
        PuzzleCategory.SYMBOLS -> symbols
        PuzzleCategory.OBJECTS -> objects
        PuzzleCategory.NATURE -> nature
    }

    fun getById(id: Int): Puzzle? = all.find { it.id == id }

    fun isLevelUnlocked(
        puzzleId: Int,
        category: PuzzleCategory,
        completedIds: Set<Int>
    ): Boolean {
        val puzzles = getByCategory(category)
        val index = puzzles.indexOfFirst { it.id == puzzleId }
        if (index < 0) return false
        if (index == 0) return true
        return puzzles[index - 1].id in completedIds
    }
}
