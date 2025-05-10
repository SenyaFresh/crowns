package ru.hse.edu.crowns.model.game.sudoku

typealias SumsMap = Map<Int, List<List<Int>>>

class SudokuGameState(
    val startCells: List<SudokuCell>,
    val playerCells: List<SudokuCell>,
    val sums: SumsMap
)