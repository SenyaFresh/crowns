package ru.hse.edu.crowns.model.game.sudoku

import ru.hse.edu.crowns.model.game.Position

data class SumZone(
    val sum: Int,
    val cells: List<Position>
)