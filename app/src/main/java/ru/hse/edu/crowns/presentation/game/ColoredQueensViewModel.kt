package ru.hse.edu.crowns.presentation.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.hse.edu.crowns.data.NQueensHelper
import ru.hse.edu.crowns.model.game.CellAction
import ru.hse.edu.crowns.model.game.queens.coloredqueens.ColoredQueensGameState
import ru.hse.edu.crowns.presentation.GameViewModel
import ru.hse.edu.crowns.model.game.Position
import ru.hse.edu.crowns.model.game.queens.CorrectQueenCell
import ru.hse.edu.crowns.model.game.queens.CrossCell
import ru.hse.edu.crowns.model.game.queens.WrongQueenCell
import javax.inject.Inject

@HiltViewModel
class ColoredQueensViewModel @Inject constructor(): GameViewModel() {
    var gameState by mutableStateOf(ColoredQueensGameState(emptyList(), emptyList(), emptyList()))
        private set
    private var n: Int = -1

    override fun generateLevel(n: Int, startCount: Int) {
        this.n = n
        gameState = NQueensHelper.generateColoredLevel(n, startCount)
    }

    override fun clearGameState() {
        gameState = gameState.copy(playerCells = emptyList())
    }

    override fun getHint() {
        val hint = NQueensHelper.generateHintForColored(n, getQueensPositions(), gameState.colors)
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
                                    if (NQueensHelper.isValidMoveForColored(
                                            action.position,
                                            getQueensPositions(),
                                            gameState.colors
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
    }

    private fun getQueensPositions(): List<Position> {
        return (gameState.playerCells + gameState.startCells)
            .filter { cell ->
                cell is CorrectQueenCell || cell is WrongQueenCell
            }
            .map { it.position }
    }
}