package ru.hse.edu.components

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimensions(
    val default: Dp = 16.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val semiMedium: Dp = 12.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 32.dp,
    val extraLarge: Dp = 64.dp
)

/**
 * A CompositionLocal for providing default spacing dimensions across the composable hierarchy.
 *
 * This is used to allow different composables to access and use the same spacing values without having to pass them explicitly.
 */
val LocalSpacing = compositionLocalOf { Dimensions() }