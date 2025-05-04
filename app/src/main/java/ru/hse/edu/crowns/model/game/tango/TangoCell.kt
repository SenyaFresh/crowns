package ru.hse.edu.crowns.model.game.tango

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ModeNight
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import ru.hse.edu.crowns.R
import ru.hse.edu.crowns.model.game.Cell
import ru.hse.edu.crowns.model.game.Position

data class TangoCell(
    override val position: Position,
    val isSun: Boolean,
    val isCorrect: Boolean = true
) : Cell(
    position,
    content = {
        Icon(
            modifier = Modifier.fillMaxSize(0.8f),
            imageVector = if (isSun) Icons.Filled.WbSunny
            else Icons.Filled.ModeNight,
            contentDescription = null,
            tint = if (!isCorrect) MaterialTheme.colorScheme.error
            else if (!isSun) Color(0xFF1855aa)
            else Color(0xFFffb31e)
        )
    }
)