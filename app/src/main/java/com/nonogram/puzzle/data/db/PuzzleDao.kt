package com.nonogram.puzzle.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nonogram.puzzle.data.model.PuzzleProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface PuzzleDao {

    @Query("SELECT * FROM puzzle_progress WHERE puzzleId = :id")
    suspend fun getProgress(id: Int): PuzzleProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: PuzzleProgress)

    @Query("SELECT COUNT(*) FROM puzzle_progress WHERE isCompleted = 1")
    fun getCompletedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM puzzle_progress WHERE isCompleted = 1 AND category = :cat")
    fun getCompletedCountByCategory(cat: String): Flow<Int>

    @Query("SELECT * FROM puzzle_progress WHERE isCompleted = 1")
    suspend fun getAllCompleted(): List<PuzzleProgress>

    @Query("SELECT MIN(timeSpent) FROM puzzle_progress WHERE puzzleId = :id")
    suspend fun getBestTime(id: Int): Long?

    @Query("SELECT SUM(timeSpent) FROM puzzle_progress WHERE isCompleted = 1")
    fun getTotalTime(): Flow<Long?>

    @Query("SELECT COUNT(*) FROM puzzle_progress WHERE isCompleted = 1 AND stars = 3")
    fun getPerfectCount(): Flow<Int>

    @Query("SELECT * FROM puzzle_progress WHERE isCompleted = 1 ORDER BY timeSpent ASC LIMIT 10")
    suspend fun getFastestSolves(): List<PuzzleProgress>

    @Query("DELETE FROM puzzle_progress WHERE puzzleId = :id")
    suspend fun deleteProgress(id: Int)

    @Query("SELECT * FROM puzzle_progress")
    fun getAllProgress(): Flow<List<PuzzleProgress>>

    @Query("DELETE FROM puzzle_progress")
    suspend fun clearAllProgress()
}
