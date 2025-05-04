package ru.hse.edu.crowns.model.game

import androidx.compose.runtime.Composable

abstract class Cell(
    open val position: Position,
    val content: @Composable () -> Unit
)