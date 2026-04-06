package com.nonogram.puzzle.data.model

data class Puzzle(
    val id: Int,
    val levelNumber: Int,
    val title: String,
    val category: PuzzleCategory,
    val difficulty: Difficulty,
    val gridSize: Int,
    val solution: List<List<Int>>,
    val rowClues: List<List<Int>>,
    val colClues: List<List<Int>>,
    val isFromCloud: Boolean = false
)
