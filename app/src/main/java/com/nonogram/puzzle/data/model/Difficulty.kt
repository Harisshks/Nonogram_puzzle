package com.nonogram.puzzle.data.model

enum class Difficulty { EASY, MEDIUM, HARD, EXPERT }

fun Difficulty.toDisplayLabel(): String = when (this) {
    Difficulty.EASY -> "Easy"
    Difficulty.MEDIUM -> "Medium"
    Difficulty.HARD -> "Hard"
    Difficulty.EXPERT -> "Expert"
}
