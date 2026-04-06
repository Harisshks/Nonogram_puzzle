package com.nonogram.puzzle.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "puzzle_progress")
data class PuzzleProgress(
    @PrimaryKey val puzzleId: Int,
    val category: String,
    val cellStates: String,
    val timeSpent: Long,
    val isCompleted: Boolean,
    val stars: Int,
    val hintsUsed: Int,
    val errors: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)
