package ru.hse.edu.crowns.model.game.queens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import ru.hse.edu.crowns.R
import ru.hse.edu.crowns.model.game.Cell
import ru.hse.edu.crowns.model.game.Position

data class WrongQueenCell(override val position: Position) :
    Cell(
        position,
        content = {
            Icon(
                modifier = Modifier.fillMaxSize(0.8f),
                imageVector = ImageVector.vectorResource(id = R.drawable.crown_icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        }
    )