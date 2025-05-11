package ru.hse.edu.crowns.model.game.sudoku


data class SudokuGameState(
    val startCells: List<SudokuCell>,
    val playerCells: List<SudokuCell>,
    val sums: List<SumZone>
)