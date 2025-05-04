package ru.hse.edu.crowns.model.game.queens.coloredqueens

import ru.hse.edu.crowns.model.game.Cell

typealias ColorsMap = List<List<Int>>

data class ColoredQueensGameState(
    val startCells: List<Cell>,
    val playerCells: List<Cell>,
    val colors: ColorsMap
)