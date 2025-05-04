package ru.hse.edu.components.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.hse.edu.components.presentation.Difficulty.Companion.getNextDifficulty
import ru.hse.edu.components.presentation.Difficulty.Easy

@Composable
fun GameCard(
    modifier: Modifier = Modifier,
    title: String,
    gameIconPainter: Painter,
    onPlayClick: (Difficulty) -> Unit
) {
    var difficulty: Difficulty by remember { mutableStateOf(Easy) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(180.dp),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
        ) {
            Text(
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 24.dp),
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        modifier = Modifier,
                        painter = gameIconPainter,
                        contentDescription = null
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    DifficultyButton(
                        difficulty = difficulty,
                        onClick = { difficulty = getNextDifficulty(difficulty) }
                    )

                    PrimaryButton(text = "Играть", onClick = { onPlayClick(difficulty) })
                }
            }
        }
    }
}

@Composable
fun DifficultyButton(
    modifier: Modifier = Modifier,
    difficulty: Difficulty,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        elevation = ButtonDefaults.buttonElevation(2.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = difficulty.color)
    ) {
        Text(
            text = difficulty.label,
            color = Color.White,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}


sealed class Difficulty(
    val label: String,
    val color: Color,
    val n: Int,
    val time: Int,
    val tips: Int,
    val startCount: Int
) {
    data object Easy : Difficulty(
        label = "Легко",
        color = Color(0xFF449E48),
        n = 6,
        time = 120,
        tips = 2,
        startCount = 1
    )

    data object Medium : Difficulty(
        label = "Средне",
        color = Color(0xFFFFA500),
        n = 6,
        time = 90,
        tips = 1,
        startCount = 1
    )

    data object Hard : Difficulty(
        label = "Сложно",
        color = Color(0xFF99231D),
        n = 8,
        time = 60,
        tips = 1,
        startCount = 3
    )

    companion object {
        fun getNextDifficulty(difficulty: Difficulty): Difficulty {
            return when (difficulty) {
                Easy -> Medium
                Medium -> Hard
                Hard -> Easy
            }
        }

        fun valueOf(string: String?): Difficulty {
            return when (string) {
                Easy.label -> Easy
                Medium.label -> Medium
                Hard.label -> Hard
                else -> Easy
            }
        }
    }
}