package ru.hse.edu.crowns.presentation.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.hse.edu.crowns.data.NQueensHelper
import ru.hse.edu.crowns.model.game.CellAction
import ru.hse.edu.crowns.model.game.queens.NQueensGameState
import ru.hse.edu.crowns.model.game.Position
import ru.hse.edu.crowns.model.game.queens.CorrectQueenCell
import ru.hse.edu.crowns.model.game.queens.CrossCell
import ru.hse.edu.crowns.model.game.queens.WrongQueenCell
import javax.inject.Inject

@HiltViewModel
class NQueensGameViewModel @Inject constructor() : GameViewModel() {
    override var isWin = mutableStateOf(false)
        private set

    var gameState by mutableStateOf(NQueensGameState(emptyList(), emptyList()))
        private set
    private var n: Int = -1

    override fun generateLevel(n: Int, startCount: Int) {
        this.n = n
        gameState = NQueensHelper.generateDefaultLevel(n, startCount)
    }

    override fun clearGameState() {
        gameState = gameState.copy(playerCells = emptyList())
    }

    override fun getHint() {
        val hint = NQueensHelper.generateHint(n, getQueensPositions())
        if (hint != null) {
            gameState = gameState.copy(
                playerCells = gameState.playerCells.plus(CorrectQueenCell(hint))
            )
        } else {
            toaster.showToast("Подсказка: минимум один из ферзей стоит неправильно.")
        }
    }

    override fun onCellAction(action: CellAction) {
        gameState.startCells.find { it.position == action.position }
            ?.let {
                toaster.showToast("Нельзя изменять стартовые поля.")
                return
            }
        gameState.playerCells.find { it.position == action.position }
            .let { oldCell ->
                when (oldCell) {
                    null -> {
                        gameState = gameState.copy(
                            playerCells = gameState.playerCells.plus(
                                CrossCell(action.position)
                            )
                        )
                    }

                    is CrossCell -> {
                        gameState = gameState.copy(
                            playerCells = gameState.playerCells.map {
                                if (it.position == action.position) {
                                    if (NQueensHelper.isValidMove(
                                            action.position,
                                            getQueensPositions()
                                        )
                                    ) {
                                        CorrectQueenCell(action.position)
                                    } else {
                                        WrongQueenCell(action.position)
                                    }
                                } else {
                                    it
                                }
                            }
                        )
                    }

                    is CorrectQueenCell,
                    is WrongQueenCell -> {
                        gameState = gameState.copy(
                            playerCells = gameState.playerCells.minus(oldCell)
                        )
                    }
                }
            }
        if (gameState.playerCells.filterIsInstance<CorrectQueenCell>().size == n - gameState.startCells.size) {
            isWin.value = true
        }
    }

    private fun getQueensPositions(): List<Position> {
        return (gameState.playerCells + gameState.startCells)
            .filter { cell ->
                cell is CorrectQueenCell || cell is WrongQueenCell
            }
            .map { it.position }
    }

}

val colorsList = listOf(
    Color(0xFFA6BCD3), // пастельный небесно-синий
    Color(0xFFF8C695), // пастельный оранжевый
    Color(0xFFF0ABAC), // пастельный коралловый
    Color(0xFFBADBD8), // пастельный бирюзово-зелёный
    Color(0xFFACD0A7), // пастельный светло-зелёный
    Color(0xFFF6E4A3), // пастельный лимонный
    Color(0xFFD7BCD0), // пастельный лавандовый
    Color(0xFFFFCED3)  // пастельный розово-персиковый
)