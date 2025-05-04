package ru.hse.edu.crowns.presentation.game

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
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import ru.hse.edu.crowns.model.game.GameType
import ru.hse.edu.crowns.model.game.Position
import ru.hse.edu.crowns.model.game.queens.CorrectQueenCell
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    difficulty: Difficulty,
    gameType: GameType,
    onExit: () -> Unit
) {
    var gameSessionState by remember { mutableStateOf(GameSessionState.GOING) }
    val viewModel = when (gameType) {
        GameType.COLORED_QUEENS -> hiltViewModel<ColoredQueensViewModel>()
        else -> hiltViewModel<ColoredQueensViewModel>()
    }

    val configuration = LocalConfiguration.current
    val cellSize = remember(configuration) {
        (configuration.screenWidthDp.dp - 32.dp) / difficulty.n
    }

    var showDialog by remember { mutableStateOf(false) }

    if (!showDialog) {
        BackHandler {
            gameSessionState = GameSessionState.EXIT
            showDialog = true
        }
    }

    var timeLeft by remember { mutableIntStateOf(difficulty.time) }
    var tipsLeft by remember { mutableIntStateOf(difficulty.tips) }

    LaunchedEffect(Unit) {
        viewModel.generateLevel(difficulty.n, difficulty.startCount)
        while (timeLeft > 0 && gameSessionState != GameSessionState.WIN) {
            delay(1.seconds)
            timeLeft--
        }
        if (!showDialog) {
            gameSessionState = GameSessionState.TIME_ENDED
            showDialog = true
        }
    }

    LaunchedEffect(viewModel.gameState.playerCells) {
        if (viewModel.gameState.playerCells.filterIsInstance<CorrectQueenCell>().size == difficulty.n - difficulty.startCount
            && !showDialog
        ) {
            gameSessionState = GameSessionState.WIN
            showDialog = true
        }
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
                        val position = Position(row, column)
                        Box(
                            modifier = Modifier
                                .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
                                .size(cellSize)
                                .clickable { viewModel.onCellAction(CellAction(position)) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (gameType == GameType.COLORED_QUEENS) {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(if (viewModel.gameState.colors.isEmpty()) Color.Transparent
                                        else colorsList[viewModel.gameState.colors[row][column]])
                                )
                            }
                            viewModel.gameState.startCells.find { it.position == position }
                                ?.let {
                                    if (gameType == GameType.N_QUEENS) {
                                        Spacer(
                                            Modifier
                                                .fillMaxSize()
                                                .background(Color.Gray.copy(alpha = 0.5f))
                                        )
                                    }
                                    it.content.invoke()
                                }
                                ?: viewModel.gameState.playerCells.find { it.position == position }?.content?.invoke()
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

        val howToPlayText = remember(gameType) {
            when (gameType) {
                GameType.COLORED_QUEENS -> "1. Ваша цель – обеспечить наличие только одного ферзя в каждой строке, столбце, диагонали и цветовой области.\n" +
                        "2. Коснитесь один раз, чтобы разместить X, и два раза, чтобы разместить Ферзя. Используйте X, чтобы отмечать места, в которых нельзя разместить Ферзя.\n" +
                        "3. Два Ферзя не могут касаться друг друга, даже по диагонали."

                else -> "1. Ваша цель – обеспечить наличие только одного ферзя в каждой строке, столбце и диагонали.\n" +
                    "2. Коснитесь один раз, чтобы разместить X, и два раза, чтобы разместить Ферзя. Используйте X, чтобы отмечать места, в которых нельзя разместить Ферзя.\n" +
                    "3. Два Ферзя не могут касаться друг друга, даже по диагонали."
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
                    text = howToPlayText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (showDialog && gameSessionState != GameSessionState.GOING) {
            BasicAlertDialog(onDismissRequest = {
                if (gameSessionState == GameSessionState.EXIT) {
                    gameSessionState = GameSessionState.GOING
                    showDialog = false
                }
            }) {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val title = remember(gameSessionState) {
                            when (gameSessionState) {
                                GameSessionState.WIN -> "Победа!"
                                GameSessionState.TIME_ENDED -> "Время вышло!"
                                else -> "Покинуть уровень?"
                            }
                        }
                        val bodyText = remember(gameSessionState) {
                            when (gameSessionState) {
                                GameSessionState.WIN -> "Поздравляем, вы победили! Вы заработали стока та монет."
                                GameSessionState.TIME_ENDED -> "К сожалению, вы проиграли и не заработали монет."
                                else -> "Вы уверены, что хотите выйти? Весь прогресс будет потерян."
                            }
                        }
                        val positiveButtonText = remember(gameSessionState) {
                            when (gameSessionState) {
                                GameSessionState.WIN,
                                GameSessionState.TIME_ENDED -> "Выйти"

                                else -> "Да"
                            }
                        }
                        Text(
                            modifier = Modifier.padding(vertical = 4.dp),
                            text = title,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.inverseSurface
                        )
                        Text(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            text = bodyText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.inverseSurface
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            if (gameSessionState == GameSessionState.EXIT) {
                                SecondaryButton(text = "Отмена", modifier = Modifier.weight(1f)) {
                                    gameSessionState = GameSessionState.GOING
                                    showDialog = false
                                }
                                Spacer(modifier = Modifier.weight(0.1f))
                            }
                            PrimaryButton(
                                text = positiveButtonText,
                                modifier = Modifier.weight(1f)
                            ) {
                                showDialog = false
                                onExit()
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class GameSessionState {
    GOING,
    WIN,
    TIME_ENDED,
    EXIT
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    GameScreen(Difficulty.Hard, GameType.COLORED_QUEENS) {}
}