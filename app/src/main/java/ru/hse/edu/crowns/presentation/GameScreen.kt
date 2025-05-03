package ru.hse.edu.crowns.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import ru.hse.edu.components.presentation.Difficulty
import ru.hse.edu.components.presentation.PrimaryButton
import ru.hse.edu.components.presentation.SecondaryButton
import ru.hse.edu.crowns.model.game.CellAction
import kotlin.time.Duration.Companion.seconds

@Composable
fun GameScreen(
    difficulty: Difficulty,
    onTimeEnd: () -> Unit,
    onExit: () -> Unit
) {
    val viewModel = hiltViewModel<GameViewModel>()

    val configuration = LocalConfiguration.current
    val cellSize = remember(configuration) {
        (configuration.screenWidthDp.dp - 32.dp) / difficulty.n
    }

    BackHandler {
        onExit()
    }

    var timeLeft by remember { mutableIntStateOf(difficulty.time) }
    var tipsLeft by remember { mutableIntStateOf(difficulty.tips) }

    LaunchedEffect(Unit) {
        viewModel.generateLevel(difficulty.n, difficulty.startCount)
        while (timeLeft > 0) {
            delay(1.seconds)
            timeLeft--
        }
        onTimeEnd()
    }

    // Таймер и подсказки.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(color = MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(40.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Card(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Outlined.Timer,
                        null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.offset(y = (-1).dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${timeLeft / 60}" +
                                ":${(timeLeft % 60).let { if (it < 10) "0$it" else it.toString() }}",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Card(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Подсказок: ",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$tipsLeft",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Игровое поле
        Column(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    2.dp,
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            repeat(difficulty.n) { row ->
                Row(
                    modifier = Modifier
                ) {
                    repeat(difficulty.n) { column ->
                        Box(
                            modifier = Modifier
                                .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
                                .size(cellSize)
                                .clickable { viewModel.onCellAction(CellAction(row, column)) },
                            contentAlignment = Alignment.Center
                        ) {
                            viewModel.gameState.startCells.find { it.row == row && it.column == column }
                                ?.let {
                                    Spacer(
                                        Modifier
                                            .fillMaxSize()
                                            .background(Color.Gray.copy(alpha = 0.5f))
                                    )
                                    it.content.invoke()
                                }
                                ?: viewModel.gameState.playerCells.find { it.row == row && it.column == column }?.content?.invoke()
                        }
                    }
                }
            }
        }

        // Выход и взятие подсказки
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(40.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SecondaryButton(
                modifier = Modifier.weight(1f),
                text = "Очистить",
                onClick = viewModel::clearGameState
            )

            Spacer(modifier = Modifier.width(8.dp))

            PrimaryButton(
                modifier = Modifier.weight(1f),
                text = "Подсказка",
                activated = tipsLeft > 0
            ) {
                tipsLeft--
                viewModel.getHint()
            }
        }

        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    modifier = Modifier.padding(start = 14.dp, top = 14.dp, end = 14.dp),
                    text = "Как играть",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.inverseSurface
                )

                Text(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                    text = "1. Ваша цель – обеспечить наличие только одного ферзя в каждой строке, столбце и диагонали.\n" +
                            "2. Коснитесь один раз, чтобы разместить X, и два раза, чтобы разместить Ферзя. Используйте X, чтобы отмечать места, в которых нельзя разместить Ферзя.\n" +
                            "3. Два Ферзя не могут касаться друг друга, даже по диагонали.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    GameScreen(Difficulty.Hard, {}, {})
}