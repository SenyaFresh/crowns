package ru.hse.edu.crowns.model.game.tango

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import ru.hse.edu.crowns.R
import ru.hse.edu.crowns.model.game.Cell
import ru.hse.edu.crowns.model.game.Position

data class TangoCell(override val position: Position, val isSun: Boolean, val isCorrect: Boolean = true) : Cell(
    position,
    content = {
        Icon(
            modifier = Modifier.fillMaxSize(0.8f),
            imageVector = if (isSun) ImageVector.vectorResource(id = R.drawable.sun_icon)
            else ImageVector.vectorResource(id = R.drawable.moon_icon),
            contentDescription = null,
            tint = if (!isCorrect) MaterialTheme.colorScheme.error else Color.Transparent
        )
    }
)