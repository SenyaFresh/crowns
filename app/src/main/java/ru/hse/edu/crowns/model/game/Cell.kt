package ru.hse.edu.crowns.model.game

import androidx.compose.runtime.Composable

abstract class Cell(
    open val row: Int,
    open val column: Int,
    val content: @Composable () -> Unit
)