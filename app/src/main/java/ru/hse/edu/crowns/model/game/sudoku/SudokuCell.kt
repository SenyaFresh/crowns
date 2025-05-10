package ru.hse.edu.crowns.model.game.sudoku

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import ru.hse.edu.crowns.model.game.Cell
import ru.hse.edu.crowns.model.game.Position

data class SudokuCell(
    override val position: Position,
    val number: Int,
    val isCorrect: Boolean = true
) : Cell(
    position,
    content = {
        Text(
            modifier = Modifier.fillMaxSize(),
            text = number.toString(),
            fontSize = MaterialTheme.typography.displaySmall.fontSize,
            color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            style = TextStyle.Default.copy(platformStyle = PlatformTextStyle(includeFontPadding = false))
        )
    }
)