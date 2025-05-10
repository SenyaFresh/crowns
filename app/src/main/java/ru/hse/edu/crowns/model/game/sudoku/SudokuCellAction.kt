package ru.hse.edu.crowns.model.game.sudoku

import ru.hse.edu.crowns.model.game.Position

data class SudokuCellAction(val position: Position, val value: Int)