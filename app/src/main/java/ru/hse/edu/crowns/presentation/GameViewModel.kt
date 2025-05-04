package ru.hse.edu.crowns.presentation

import ru.hse.edu.components.BaseViewModel
import ru.hse.edu.crowns.model.game.CellAction


abstract class GameViewModel: BaseViewModel() {

    abstract fun generateLevel(n: Int, startCount: Int)

    abstract fun clearGameState()

    abstract fun getHint()

    abstract fun onCellAction(action: CellAction)
}