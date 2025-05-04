package ru.hse.edu.crowns.model.game.tango

import ru.hse.edu.crowns.model.game.Cell
import ru.hse.edu.crowns.model.game.Position

data class TangoCondition(
    val firstPosition: Position,
    val secondPosition: Position,
    val equal: Boolean
)