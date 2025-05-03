package ru.hse.edu.crowns.model.game.queens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import ru.hse.edu.crowns.model.game.Cell

data class CrossCell(override val row: Int, override val column: Int) :
    Cell(
        row,
        column,
        content = {
            Icon(
                modifier = Modifier.fillMaxSize(0.4f),
                imageVector = Icons.Rounded.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )