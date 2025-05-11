package ru.hse.edu.crowns.presentation.game

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DragHandle
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import ru.hse.edu.components.presentation.Difficulty
import ru.hse.edu.components.presentation.PrimaryButton
import ru.hse.edu.components.presentation.SecondaryButton
import ru.hse.edu.crowns.R
import ru.hse.edu.crowns.data.AccountsHelper
import ru.hse.edu.crowns.model.game.CellAction
import ru.hse.edu.crowns.model.game.GameType
import ru.hse.edu.crowns.model.game.Position
import ru.hse.edu.crowns.model.game.sudoku.SudokuCellAction
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
        GameType.KILLER_SUDOKU -> hiltViewModel<KillerSudokuViewModel>()
        GameType.TANGO -> hiltViewModel<TangoViewModel>()
        GameType.N_QUEENS -> hiltViewModel<NQueensGameViewModel>()
    }

    val n = if (gameType == GameType.KILLER_SUDOKU) 9 else difficulty.n

    val configuration = LocalConfiguration.current
    val cellSize = remember(configuration) {
        (configuration.screenWidthDp.dp - 32.dp) / n
    }

    var showDialog by remember { mutableStateOf(false) }

    if (!showDialog) {
        BackHandler {
            gameSessionState = GameSessionState.EXIT
            showDialog = true
        }
    }

    var number by remember { mutableIntStateOf(0) }

    var timeLeft by remember { mutableIntStateOf(difficulty.time * if (gameType == GameType.KILLER_SUDOKU) 2 else 1) }
    var tipsLeft by remember { mutableIntStateOf(difficulty.tips) }

    LaunchedEffect(Unit) {
        viewModel.generateLevel(n, difficulty)
        while (timeLeft > 0 && gameSessionState != GameSessionState.WIN) {
            delay(1.seconds)
            timeLeft--
        }
        if (!showDialog) {
            gameSessionState = GameSessionState.TIME_ENDED
            showDialog = true
        }
    }

    LaunchedEffect(viewModel.isWin.value) {
        if (viewModel.isWin.value && !showDialog) {
            gameSessionState = GameSessionState.WIN
            showDialog = true
        }
    }

    var customBackground by remember { mutableStateOf(true) }

    if (!customBackground) {
        Spacer(modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.surface))
    } else {
        val activity = LocalContext.current as Activity
        WindowCompat.getInsetsController(
            activity.window,
            activity.window.decorView
        ).apply {
            isAppearanceLightStatusBars = false
        }
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.space_bg),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }

    // Таймер и подсказки.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp)
            .verticalScroll(rememberScrollState()),
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
        Box(
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .padding(vertical = 12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color = MaterialTheme.colorScheme.surface)
                .border(
                    2.dp,
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Column {
                repeat(n) { row ->
                    Row(
                        modifier = Modifier
                    ) {
                        repeat(n) { column ->
                            val position = Position(row, column)
                            Box(
                                modifier = Modifier
                                    .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
                                    .size(cellSize)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        if (viewModel is KillerSudokuViewModel) {
                                            viewModel.onSudokuCellAction(
                                                SudokuCellAction(
                                                    position,
                                                    number
                                                )
                                            )
                                        } else viewModel.onCellAction(CellAction(position))
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                when (viewModel) {
                                    is ColoredQueensViewModel -> {
                                        Spacer(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    if (viewModel.gameState.colors.isEmpty()) Color.Transparent
                                                    else colorsList[viewModel.gameState.colors[row][column]]
                                                )
                                        )
                                        viewModel.gameState.startCells.find { it.position == position }?.content?.invoke()
                                            ?: viewModel.gameState.playerCells.find { it.position == position }?.content?.invoke()
                                    }

                                    is KillerSudokuViewModel -> {
                                        viewModel.gameState.startCells.find { it.position == position }
                                            ?.let {
                                                Spacer(
                                                    Modifier
                                                        .fillMaxSize()
                                                        .background(Color.Gray.copy(alpha = 0.5f))
                                                )
                                                it.content.invoke()
                                            }
                                            ?: viewModel.gameState.playerCells.find { it.position == position }?.content?.invoke()
                                    }

                                    is TangoViewModel -> {
                                        viewModel.gameState.startCells.find { it.position == position }
                                            ?.let {
                                                Spacer(
                                                    Modifier
                                                        .fillMaxSize()
                                                        .background(Color.Gray.copy(alpha = 0.25f))
                                                )
                                                it.content.invoke()
                                            }
                                            ?: viewModel.gameState.playerCells.find { it.position == position }?.content?.invoke()
                                    }

                                    is NQueensGameViewModel -> {
                                        viewModel.gameState.startCells.find { it.position == position }
                                            ?.let {
                                                Spacer(
                                                    Modifier
                                                        .fillMaxSize()
                                                        .background(Color.Gray.copy(alpha = 0.5f))
                                                )
                                                it.content.invoke()
                                            }
                                            ?: viewModel.gameState.playerCells.find { it.position == position }?.content?.invoke()
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (viewModel is TangoViewModel) {
                viewModel.gameState.conditions.forEach {
                    val x =
                        ((it.firstPosition.column + it.secondPosition.column).toDouble() / 2 * cellSize.value + cellSize.value / 2).dp
                    val y =
                        ((it.firstPosition.row + it.secondPosition.row).toDouble() / 2 * cellSize.value + cellSize.value / 2).dp
                    Icon(
                        imageVector = if (it.equal) Icons.Rounded.DragHandle else Icons.Rounded.Close,
                        contentDescription = null,
                        modifier = Modifier
                            .offset(x - cellSize / 8, y - cellSize / 8)
                            .size(cellSize / 4)
                            .background(MaterialTheme.colorScheme.surface, CircleShape),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (viewModel is KillerSudokuViewModel) {
                val strokeColor = MaterialTheme.colorScheme.outline
                val strokeWidth = 1.dp.dpToPx()
                val dash = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 3f)
                val wallSize = (cellSize * 0.8f).dpToPx()
                val offset = (cellSize * 0.1f).dpToPx()
                val directions = listOf(0 to 1, 1 to 0, 0 to -1, -1 to 0)
                viewModel.gameState.sums.forEach { (sum, cells) ->

                    cells.forEach { cell ->
                        // право, низ, лево, верх
                        val directionsWalls = mutableListOf(true, true, true, true)
                        directions.forEachIndexed { idx, (dr, dc) ->
                            val nr = cell.row + dr
                            val nc = cell.column + dc
                            if (cells.any { it.row == nr && it.column == nc }) {
                                directionsWalls[idx] = false
                            }
                        }
                        val baseX = (cell.column * cellSize).dpToPx() + offset
                        val baseY = (cell.row * cellSize).dpToPx() + offset
                        Canvas(modifier = Modifier.matchParentSize()) {
                            // право
                            if (directionsWalls[0]) {
                                drawLine(
                                    color = strokeColor,
                                    strokeWidth = strokeWidth,
                                    start = Offset(baseX + wallSize, baseY),
                                    end = Offset(baseX + wallSize, baseY + wallSize),
                                    pathEffect = dash
                                )
                                // вверх
                                if (!directionsWalls[3]) {
                                    drawLine(
                                        color = strokeColor,
                                        strokeWidth = strokeWidth,
                                        start = Offset(baseX + wallSize, baseY),
                                        end = Offset(baseX + wallSize, baseY - offset),
                                        pathEffect = dash
                                    )
                                }
                                // вниз
                                if (!directionsWalls[1]) {
                                    drawLine(
                                        color = strokeColor,
                                        strokeWidth = strokeWidth,
                                        start = Offset(baseX + wallSize, baseY + wallSize),
                                        end = Offset(baseX + wallSize, baseY + wallSize + offset),
                                        pathEffect = dash
                                    )
                                }
                            }

                            // низ
                            if (directionsWalls[1]) {
                                drawLine(
                                    color = strokeColor,
                                    strokeWidth = strokeWidth,
                                    start = Offset(baseX, baseY + wallSize),
                                    end = Offset(baseX + wallSize, baseY + wallSize),
                                    pathEffect = dash
                                )
                                // влево
                                if (!directionsWalls[2]) {
                                    drawLine(
                                        color = strokeColor,
                                        strokeWidth = strokeWidth,
                                        start = Offset(baseX, baseY + wallSize),
                                        end = Offset(baseX - offset, baseY + wallSize),
                                        pathEffect = dash
                                    )
                                }
                                // вправо
                                if (!directionsWalls[0]) {
                                    drawLine(
                                        color = strokeColor,
                                        strokeWidth = strokeWidth,
                                        start = Offset(baseX + wallSize, baseY + wallSize),
                                        end = Offset(baseX + wallSize + offset, baseY + wallSize),
                                        pathEffect = dash
                                    )
                                }
                            }

                            // лево
                            if (directionsWalls[2]) {
                                drawLine(
                                    color = strokeColor,
                                    strokeWidth = strokeWidth,
                                    start = Offset(baseX, baseY),
                                    end = Offset(baseX, baseY + wallSize),
                                    pathEffect = dash
                                )
                                // вверх
                                if (!directionsWalls[3]) {
                                    drawLine(
                                        color = strokeColor,
                                        strokeWidth = strokeWidth,
                                        start = Offset(baseX, baseY),
                                        end = Offset(baseX, baseY - offset),
                                        pathEffect = dash
                                    )
                                }
                                // вниз
                                if (!directionsWalls[1]) {
                                    drawLine(
                                        color = strokeColor,
                                        strokeWidth = strokeWidth,
                                        start = Offset(baseX, baseY + wallSize),
                                        end = Offset(baseX, baseY + wallSize + offset),
                                        pathEffect = dash
                                    )
                                }
                            }

                            // верх
                            if (directionsWalls[3]) {
                                drawLine(
                                    color = strokeColor,
                                    strokeWidth = strokeWidth,
                                    start = Offset(baseX, baseY),
                                    end = Offset(baseX + wallSize, baseY),
                                    pathEffect = dash
                                )
                                // влево
                                if (!directionsWalls[2]) {
                                    drawLine(
                                        color = strokeColor,
                                        strokeWidth = strokeWidth,
                                        start = Offset(baseX, baseY),
                                        end = Offset(baseX - offset, baseY),
                                        pathEffect = dash
                                    )
                                }
                                // вправо
                                if (!directionsWalls[0]) {
                                    drawLine(
                                        color = strokeColor,
                                        strokeWidth = strokeWidth,
                                        start = Offset(baseX + wallSize, baseY),
                                        end = Offset(baseX + wallSize + offset, baseY),
                                        pathEffect = dash
                                    )
                                }
                            }

                            // Левый верхний угол
                            if (!directionsWalls[2] && !directionsWalls[3] && !cells.any { it.row == cell.row - 1 && it.column == cell.column - 1 }) {
                                drawLine(
                                    color = strokeColor,
                                    strokeWidth = strokeWidth,
                                    start = Offset(baseX - offset, baseY),
                                    end = Offset(baseX, baseY),
                                    pathEffect = dash
                                )
                                drawLine(
                                    color = strokeColor,
                                    strokeWidth = strokeWidth,
                                    start = Offset(baseX, baseY - offset),
                                    end = Offset(baseX, baseY),
                                    pathEffect = dash
                                )
                            }

                            // Правый верхний угол
                            if (!directionsWalls[0] && !directionsWalls[3] && !cells.any { it.row == cell.row - 1 && it.column == cell.column + 1 }) {
                                drawLine(
                                    color = strokeColor,
                                    strokeWidth = strokeWidth,
                                    start = Offset(baseX + wallSize + offset, baseY),
                                    end = Offset(baseX + wallSize, baseY),
                                    pathEffect = dash
                                )
                                drawLine(
                                    color = strokeColor,
                                    strokeWidth = strokeWidth,
                                    start = Offset(baseX + wallSize, baseY - offset),
                                    end = Offset(baseX + wallSize, baseY),
                                    pathEffect = dash
                                )
                            }

                            // Правый нижний угол
                            if (!directionsWalls[0] && !directionsWalls[1] && !cells.any { it.row == cell.row + 1 && it.column == cell.column + 1 }) {
                                drawLine(
                                    color = strokeColor,
                                    strokeWidth = strokeWidth,
                                    start = Offset(baseX + wallSize + offset, baseY + wallSize),
                                    end = Offset(baseX + wallSize, baseY + wallSize),
                                    pathEffect = dash
                                )
                                drawLine(
                                    color = strokeColor,
                                    strokeWidth = strokeWidth,
                                    start = Offset(baseX + wallSize, baseY + wallSize + offset),
                                    end = Offset(baseX + wallSize, baseY + wallSize),
                                    pathEffect = dash
                                )
                            }

                            // Левый нижний угол
                            if (!directionsWalls[2] && !directionsWalls[1] && !cells.any { it.row == cell.row + 1 && it.column == cell.column - 1 }) {
                                drawLine(
                                    color = strokeColor,
                                    strokeWidth = strokeWidth,
                                    start = Offset(baseX - offset, baseY + wallSize),
                                    end = Offset(baseX, baseY + wallSize),
                                    pathEffect = dash
                                )
                                drawLine(
                                    color = strokeColor,
                                    strokeWidth = strokeWidth,
                                    start = Offset(baseX, baseY + wallSize + offset),
                                    end = Offset(baseX, baseY + wallSize),
                                    pathEffect = dash
                                )
                            }

                        }
                    }
                    val minRow = cells.minOf { it.row }
                    val minCol = cells.filter { it.row == minRow }.minOf { it.column }
                    val isStart = viewModel.gameState.startCells.any {
                        it.position == Position(
                            minRow,
                            minCol
                        )
                    }
                    val textX = (minCol * cellSize)
                    val textY = (minRow * cellSize)
                    Text(
                        text = sum.toString(),
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = MaterialTheme.typography.labelSmall.fontSize,
                        style = TextStyle.Default.copy(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                        modifier = Modifier
                            .offset(textX, textY)
                            .padding(2.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(2.dp)
                            )
                            .background(
                                color = if (isStart) Color.Gray.copy(alpha = 0.5f) else Color.Transparent,
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
                val cellSizePx = cellSize.dpToPx()
                val mainStrokeWidth = 3.dp.dpToPx()
                val mainStrokeColor = MaterialTheme.colorScheme.onSurface
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawLine(
                        color = mainStrokeColor,
                        strokeWidth = mainStrokeWidth,
                        start = Offset(cellSizePx * 3, 0F),
                        end = Offset(cellSizePx * 3, cellSizePx * 9)
                    )
                    drawLine(
                        color = mainStrokeColor,
                        strokeWidth = mainStrokeWidth,
                        start = Offset(cellSizePx * 6, 0F),
                        end = Offset(cellSizePx * 6, cellSizePx * 9)
                    )
                    drawLine(
                        color = mainStrokeColor,
                        strokeWidth = mainStrokeWidth,
                        start = Offset(0F, cellSizePx * 3),
                        end = Offset(cellSizePx * 9, cellSizePx * 3)
                    )
                    drawLine(
                        color = mainStrokeColor,
                        strokeWidth = mainStrokeWidth,
                        start = Offset(0F, cellSizePx * 6),
                        end = Offset(cellSizePx * 9, cellSizePx * 6)
                    )
                }
            }
        }

        if (viewModel is KillerSudokuViewModel) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(40.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                (1..9).forEach { num ->
                    Box(
                        modifier = Modifier
                            .size(cellSize * 0.9f)
                            .background(
                                color = if (num == number) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable {
                                number = num
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = num.toString(),
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                            color = if (num == number) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onTertiaryContainer,
                            textAlign = TextAlign.Center,
                            style = TextStyle.Default.copy(
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            )
                        )
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
                GameType.COLORED_QUEENS ->
                    "1. Ваша цель – обеспечить наличие только одного ферзя в каждой строке, столбце, диагонали и цветовой области.\n" +
                            "2. Коснитесь один раз, чтобы разместить X, и два раза, чтобы разместить Ферзя. Используйте X, чтобы отмечать места, в которых нельзя разместить Ферзя.\n" +
                            "3. Два Ферзя не могут касаться друг друга, даже по диагонали."

                GameType.KILLER_SUDOKU ->
                    "1. Заполните числами от 1 до 9 все строки, колонки и квадраты 3х3, как в классическом судоку.\n" +
                            "2. Не забывайте о зонах сумм — наборах ячеек, выделенных пунктиром.\n" +
                            "3. Сумма цифр в зоне сумм должна быть равна числу в левом верхнем углу этой зоны.\n" +
                            "4. Внутри одной зоны сумм, строки, колонки или квадрата 3х3 каждое число можно использовать только один раз."

                GameType.TANGO ->
                    "1. Заполните сетку так, чтобы каждая клетка содержала либо Луну, либо Солнце.\n" +
                            "2. Рядом друг с другом могут находиться не более 2 Лун или Солнц ,как по вертикали, так и по горизонтали.\n" +
                            "3. Каждая строка (и столбец) должна содержать одинаковое количество Лун и Солнц.\n" +
                            "4. Клетки, разделенные знаком =, должны быть одного типа.\n" +
                            "5. Клетки, разделенные знаком X, должны быть противоположного типа."

                GameType.N_QUEENS ->
                    "1. Ваша цель – обеспечить наличие только одного ферзя в каждой строке, столбце и диагонали.\n" +
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
                        val score = remember {
                            (difficulty.time - timeLeft) * (1 + tipsLeft) * when (difficulty) {
                                Difficulty.Easy -> 1
                                Difficulty.Medium -> 2
                                Difficulty.Hard -> 3
                            }
                        }
                        LaunchedEffect(Unit) {
                            if (gameSessionState == GameSessionState.WIN) {
                                AccountsHelper.updateScore(score.toLong())
                            }
                        }
                        val bodyText = remember(gameSessionState) {
                            when (gameSessionState) {
                                GameSessionState.WIN -> "Поздравляем, вы победили!\n" +
                                        "Потраченок секунд: ${difficulty.time - timeLeft}\n" +
                                        "Потрачено подсказок: ${difficulty.tips - tipsLeft}\n" +
                                        "Вы заработали $score\uD83E\uDE99!"
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

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    GameScreen(Difficulty.Hard, GameType.COLORED_QUEENS) {}
}