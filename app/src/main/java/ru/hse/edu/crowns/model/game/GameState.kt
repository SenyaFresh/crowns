package ru.hse.edu.crowns.model.game

data class GameState(
    val startCells: List<Cell>,
    val playerCells: List<Cell>,
)