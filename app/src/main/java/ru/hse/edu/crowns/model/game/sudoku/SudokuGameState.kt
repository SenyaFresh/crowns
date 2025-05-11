package ru.hse.edu.crowns.model.game.sudoku

import ru.hse.edu.crowns.model.game.Position


data class SudokuGameState(
    val startCells: List<SudokuCell>,
    val playerCells: List<SudokuCell>,
    val sums: List<SumZone>
)