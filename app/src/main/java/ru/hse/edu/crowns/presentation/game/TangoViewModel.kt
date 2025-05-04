package ru.hse.edu.crowns.presentation.game

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.hse.edu.crowns.data.TangoHelper
import ru.hse.edu.crowns.model.game.CellAction
import ru.hse.edu.crowns.model.game.tango.TangoCell
import ru.hse.edu.crowns.model.game.tango.TangoGameState
import javax.inject.Inject

@HiltViewModel
class TangoViewModel @Inject constructor() : GameViewModel() {

    override var isWin = mutableStateOf(false)
        private set

    var gameState by mutableStateOf(TangoGameState(emptyList(), emptyList(), emptyList()))
        private set
    private var n: Int = -1

    override fun generateLevel(n: Int, startCount: Int) {
        this.n = n
        gameState = TangoHelper.generateLevel(n, startCount)
    }

    override fun clearGameState() {
        gameState = gameState.copy(playerCells = emptyList())
    }

    override fun getHint() {
        TODO("Not yet implemented")
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
                        val cell = TangoCell(action.position, false)
                        gameState = gameState.copy(
                            playerCells = gameState.playerCells.plus(
                                cell.copy(
                                    isCorrect = TangoHelper.isValidMove(
                                        cell,
                                        gameState.playerCells + gameState.startCells,
                                        gameState.conditions,
                                        n
                                    )
                                )
                            )
                        )
                    }

                    !oldCell.isSun -> {
                        gameState = gameState.copy(
                            playerCells = gameState.playerCells.map {
                                if (it.position == action.position) {
                                    val cell = TangoCell(action.position, true)
                                    cell.copy(
                                        isCorrect = TangoHelper.isValidMove(
                                            cell,
                                            gameState.playerCells + gameState.startCells,
                                            gameState.conditions,
                                            n
                                        )
                                    )
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

        if (gameState.playerCells.filter { it.isCorrect }.size == n * n - gameState.startCells.size) {
            isWin.value = true
        }

    }
}