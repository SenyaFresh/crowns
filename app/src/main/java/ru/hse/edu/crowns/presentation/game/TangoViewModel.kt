package ru.hse.edu.crowns.presentation.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.hse.edu.components.presentation.Difficulty
import ru.hse.edu.crowns.data.helpers.TangoHelper
import ru.hse.edu.crowns.model.game.CellAction
import ru.hse.edu.crowns.model.game.tango.TangoCell
import ru.hse.edu.crowns.model.game.tango.TangoGameState
import javax.inject.Inject

@HiltViewModel
class TangoViewModel @Inject constructor() : GameViewModel() {

    override val isWin = mutableStateOf(false)

    var gameState by mutableStateOf(TangoGameState(emptyList(), emptyList(), emptyList()))
        private set
    private var n: Int = -1

    override fun generateLevel(n: Int, difficulty: Difficulty) {
        this.n = n
        gameState = TangoHelper.generateLevel(n, difficulty.startCount * 10)
    }

    override fun clearGameState() {
        gameState = gameState.copy(playerCells = emptyList())
    }

    override fun getHint() {
        val hint = TangoHelper.generateHint(
            n,
            gameState.playerCells + gameState.startCells,
            gameState.conditions
        )
        if (hint != null) {
            gameState = gameState.copy(
                playerCells = gameState.playerCells.plus(hint)
            )
        } else {
            toaster.showToast("Подсказка: с текущей расстановкой нельзя найти решение.")
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
                when {
                    oldCell == null -> {
                        gameState = gameState.copy(
                            playerCells = gameState.playerCells.plus(
                                TangoCell(action.position, false)
                            )
                        )
                    }

                    !oldCell.isSun -> {
                        gameState = gameState.copy(
                            playerCells = gameState.playerCells.map {
                                if (it.position == action.position) {
                                    TangoCell(action.position, true)
                                } else {
                                    it
                                }
                            }
                        )
                    }

                    else -> {
                        gameState = gameState.copy(
                            playerCells = gameState.playerCells.minus(oldCell)
                        )
                    }
                }
            }

        gameState = gameState.copy(
            playerCells = gameState.playerCells.map {
                it.copy(
                    isCorrect = TangoHelper.isValidMove(
                        it,
                        gameState.playerCells + gameState.startCells,
                        gameState.conditions,
                        n
                    )
                )
            }
        )

        if (gameState.playerCells.filter { it.isCorrect }.size == n * n - gameState.startCells.size) {
            isWin.value = true
        }

    }
}