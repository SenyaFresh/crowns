package ru.hse.edu.crowns.model.game.queens

import ru.hse.edu.crowns.model.game.Cell

data class NQueensGameState(
    val startCells: List<Cell>,
    val playerCells: List<Cell>
)