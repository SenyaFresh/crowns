package ru.hse.edu.crowns.presentation.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.hse.edu.crowns.data.KillerSudokuHelper
import ru.hse.edu.crowns.model.game.CellAction
import ru.hse.edu.crowns.model.game.sudoku.SudokuCellAction
import ru.hse.edu.crowns.model.game.sudoku.SudokuGameState
import javax.inject.Inject

@HiltViewModel
class KillerSudokuViewModel @Inject constructor() : GameViewModel() {

    override val isWin = mutableStateOf(false)

    var gameState by mutableStateOf(SudokuGameState(emptyList(), emptyList(), emptyList()))
        private set

    override fun generateLevel(n: Int, startCount: Int) {
        gameState = KillerSudokuHelper.generateLevel(startCount * 20)
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

    }
}