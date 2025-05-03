package ru.hse.edu.crowns.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.hse.edu.components.BaseViewModel
import ru.hse.edu.crowns.model.game.CellAction
import ru.hse.edu.crowns.model.game.GameState
import ru.hse.edu.crowns.model.game.queens.CorrectQueenCell
import ru.hse.edu.crowns.model.game.queens.CrossCell
import ru.hse.edu.crowns.model.game.queens.WrongQueenCell
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor() : BaseViewModel() {
    var gameState by mutableStateOf(GameState(emptyList(), emptyList()))
        private set

    fun clearGameState() {
        gameState = gameState.copy(playerCells = emptyList())
    }

    fun onCellAction(action: CellAction) {
        gameState.startCells.find { it.row == action.row && it.column == action.column }
            ?.let {
                toaster.showToast("Нельзя изменять стартовые поля.")
                return
            }
        gameState.playerCells.find { it.row == action.row && it.column == action.column }
            .let { oldCell ->
                when (oldCell) {
                    null -> {
                        gameState = gameState.copy(
                            playerCells = gameState.playerCells.plus(
                                CrossCell(
                                    action.row,
                                    action.column
                                )
                            )
                        )
                    }
                    is CrossCell -> {
                        gameState = gameState.copy(
                            playerCells = gameState.playerCells.map {
                                if (it.row == action.row && it.column == action.column) {
                                    CorrectQueenCell(action.row, action.column)
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

}
