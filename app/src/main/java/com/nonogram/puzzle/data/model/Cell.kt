package com.nonogram.puzzle.data.model

data class Cell(
    val row: Int,
    val col: Int,
    var state: CellState = CellState.EMPTY
)
