package ru.hse.edu.crowns.model.game.tango

data class TangoGameState(
    val startCells: List<TangoCell>,
    val playerCells: List<TangoCell>,
    val conditions: List<TangoCondition>
)