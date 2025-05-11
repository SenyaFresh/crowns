package ru.hse.edu.crowns.presentation.game

import androidx.compose.runtime.State
import ru.hse.edu.components.BaseViewModel
import ru.hse.edu.components.presentation.Difficulty
import ru.hse.edu.crowns.model.game.CellAction


abstract class GameViewModel: BaseViewModel() {

    abstract val isWin: State<Boolean>

    abstract fun generateLevel(n: Int, difficulty: Difficulty)

    abstract fun clearGameState()

    abstract fun getHint()

    abstract fun onCellAction(action: CellAction)
}