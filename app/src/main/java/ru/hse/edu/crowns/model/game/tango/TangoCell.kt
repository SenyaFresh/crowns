package ru.hse.edu.crowns.model.game.tango

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
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
        Image(
            modifier = Modifier.fillMaxSize(if (isSun) 0.9f else 0.8f),
            imageVector = if (isSun) ImageVector.vectorResource(R.drawable.sun_icon)
            else ImageVector.vectorResource(R.drawable.moon_icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.error).takeIf { !isCorrect }
        )
    }
)