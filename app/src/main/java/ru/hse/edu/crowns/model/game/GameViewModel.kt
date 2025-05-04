package ru.hse.edu.crowns.model.game

import ru.hse.edu.components.BaseViewModel
import ru.hse.edu.crowns.data.NQueensHelper
import ru.hse.edu.crowns.model.game.queens.CorrectQueenCell
import ru.hse.edu.crowns.model.game.queens.CrossCell
import ru.hse.edu.crowns.model.game.queens.WrongQueenCell


abstract class GameViewModel: BaseViewModel() {

    abstract fun generateLevel(n: Int, startCount: Int)

    abstract fun clearGameState()

    abstract fun getHint()

    abstract fun onCellAction(action: CellAction)
}