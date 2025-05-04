package ru.hse.edu.crowns.model.game.tango

import ru.hse.edu.crowns.model.game.Cell

data class TangoGameState(
    val startCells: List<TangoCell>,
    val playerCells: List<TangoCell>,
    val conditions: List<TangoCondition>
)