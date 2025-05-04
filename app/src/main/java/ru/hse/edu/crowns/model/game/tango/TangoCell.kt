package ru.hse.edu.crowns.model.game.tango

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import ru.hse.edu.crowns.R
import ru.hse.edu.crowns.model.game.Cell
import ru.hse.edu.crowns.model.game.Position

class TangoCell(override val position: Position, val isSun: Boolean) : Cell(
    position,
    content = {
        Icon(
            modifier = Modifier.fillMaxSize(0.8f),
            imageVector = if (isSun) ImageVector.vectorResource(id = R.drawable.sun_icon)
            else ImageVector.vectorResource(id = R.drawable.moon_icon),
            contentDescription = null,
        )
    }
)