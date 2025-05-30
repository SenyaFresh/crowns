package ru.hse.edu.crowns.presentation.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.hse.edu.components.presentation.Difficulty
import ru.hse.edu.crowns.data.helpers.KillerSudokuHelper
import ru.hse.edu.crowns.model.game.CellAction
import ru.hse.edu.crowns.model.game.sudoku.SudokuCell
import ru.hse.edu.crowns.model.game.sudoku.SudokuCellAction
import ru.hse.edu.crowns.model.game.sudoku.SudokuGameState
import javax.inject.Inject

@HiltViewModel
class KillerSudokuViewModel @Inject constructor() : GameViewModel() {

    override val isWin = mutableStateOf(false)

    var gameState by mutableStateOf(SudokuGameState(emptyList(), emptyList(), emptyList()))
        private set

    override fun generateLevel(n: Int, difficulty: Difficulty) {
        gameState = KillerSudokuHelper.generateLevel(
            when (difficulty) {
                Difficulty.Easy -> 45
                Difficulty.Medium -> 35
                Difficulty.Hard -> 20
            }
        )
    }

    override fun clearGameState() {
        gameState = gameState.copy(playerCells = emptyList())
    }

    override fun getHint() {
        val hint = KillerSudokuHelper.generateHint(gameState.startCells + gameState.playerCells)
        if (hint != null) {
            gameState = gameState.copy(
                playerCells = gameState.playerCells.plus(hint)
            )
        } else {
            toaster.showToast("Подсказка: минимум одно из чисел стоит неправильно.")
        }
    }

    override fun onCellAction(action: CellAction) = Unit

    fun onSudokuCellAction(action: SudokuCellAction) {
        if (action.value !in (1..9)) {
            toaster.showToast("Сначала выберите число.")
            return
        }
        gameState.startCells.find { it.position == action.position }
            ?.let {
                toaster.showToast("Нельзя изменять стартовые поля.")
                return
            }
        if (gameState.playerCells.any { it.position == action.position }) {
            gameState = gameState.copy(
                playerCells = gameState.playerCells.filter { it.position != action.position }
            )
        }
        gameState = gameState.copy(
            playerCells = gameState.playerCells.plus(
                SudokuCell(
                    action.position,
                    action.value,
                    KillerSudokuHelper.lastGeneratedGrid!![action.position.row][action.position.column] == action.value
                )
            )
        )
        if (gameState.playerCells.filter { it.isCorrect }.size == 81 - gameState.startCells.size) {
            isWin.value = true
        }
    }
}