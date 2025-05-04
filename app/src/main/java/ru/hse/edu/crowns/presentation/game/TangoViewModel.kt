package ru.hse.edu.crowns.presentation.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.hse.edu.crowns.data.TangoHelper
import ru.hse.edu.crowns.model.game.CellAction
import ru.hse.edu.crowns.model.game.tango.TangoGameState
import ru.hse.edu.crowns.presentation.GameViewModel
import javax.inject.Inject

@HiltViewModel
class TangoViewModel @Inject constructor(): GameViewModel() {

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
        TODO("Not yet implemented")
    }
}