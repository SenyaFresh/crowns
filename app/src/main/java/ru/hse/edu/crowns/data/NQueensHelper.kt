package ru.hse.edu.crowns.data

import ru.hse.edu.crowns.model.game.queens.NQueensGameState
import ru.hse.edu.crowns.model.game.Position
import ru.hse.edu.crowns.model.game.queens.coloredqueens.ColoredQueensGameState
import ru.hse.edu.crowns.model.game.queens.coloredqueens.ColorsMap
import ru.hse.edu.crowns.model.game.queens.CorrectQueenCell
import kotlin.math.abs
import kotlin.random.Random

object NQueensHelper {
    fun generateColoredLevel(n: Int, startCount: Int): ColoredQueensGameState {
        val positions = generateLevel(n)
        if (positions.isEmpty()) return ColoredQueensGameState(
            emptyList(),
            emptyList(),
            emptyList()
        )

        val board = MutableList(n) { MutableList(n) { -1 } }
        val territoryFrontiers = MutableList(n) { ArrayDeque<Position>() }
        val directions = listOf(0 to 1, 1 to 0, 0 to -1, -1 to 0)
        val random = Random

        for ((color, pos) in positions.withIndex()) {
            val (row, column) = pos
            board[row][column] = color
            territoryFrontiers[color].addLast(Position(row, column))
        }

        fun freeNeighbors(position: Position) = directions
            .map { (rowOffset, columnOffset) -> position.row + rowOffset to position.column + columnOffset }
            .filter { (neighborRow, neighborColumn) ->
                neighborRow in 0 until n && neighborColumn in 0 until n
                        && board[neighborRow][neighborColumn] == -1
            }

        var unfilled = n * n - n

        while (unfilled > 0) {
            val activeColors = territoryFrontiers
                .mapIndexedNotNull { idx, queue ->
                    if (queue.any { freeNeighbors(it).isNotEmpty() }) idx else null
                }
                .shuffled(random)

            for (color in activeColors) {
                if (unfilled == 0) break

                val queue = territoryFrontiers[color]
                repeat(queue.size) {
                    val position = queue.removeFirst()
                    val neighbor = freeNeighbors(position)
                    if (neighbor.isNotEmpty()) {
                        val (neighborRow, neighborColumn) = neighbor.random(random)
                        board[neighborRow][neighborColumn] = color
                        queue.addLast(Position(neighborRow, neighborColumn))
                        if (freeNeighbors(position).isNotEmpty()) {
                            queue.addLast(position)
                        }
                        unfilled--
                        return@repeat
                    }
                }
            }
        }

        return ColoredQueensGameState(
            positions.shuffled().take(startCount).map { CorrectQueenCell(it) },
            emptyList(),
            board
        )
    }

    fun isValidMoveForColored(move: Position, positions: List<Position>, colors: ColorsMap): Boolean {
        if (!isValidMove(move, positions)) return false
        val occupiedColors = positions.map { colors[it.row][it.column] }.toSet()
        return !occupiedColors.contains(colors[move.row][move.column])
    }

    fun generateHintForColored(n: Int, positions: List<Position>, colors: ColorsMap): Position? {
        val occupiedRows = positions.map { it.row }.toSet()
        for (row in 0 until n) {
            if (row in occupiedRows) continue
            for (col in 0 until n) {
                if (!isValidMoveForColored(Position(row, col), positions, colors)) continue
                val trial = positions.toMutableList().apply { add(Position(row, col)) }
                if (canSolveFrom(n, trial)) {
                    return Position(row, col)
                }
            }
        }
        return null
    }


    fun generateDefaultLevel(n: Int, startCount: Int): NQueensGameState {
        return NQueensGameState(
            generateLevel(n).shuffled().take(startCount)
                .map { CorrectQueenCell(it) }, emptyList()
        )
    }

    fun isValidMove(move: Position, positions: List<Position>): Boolean {
        positions.forEach {
            if (it.row == move.row) return false
            if (it.column == move.column) return false
            if (abs(it.row - move.row) == abs(it.column - move.column)) return false
        }
        return true
    }

    fun generateHint(n: Int, positions: List<Position>): Position? {
        val occupiedRows = positions.map { it.row }.toSet()
        for (row in 0 until n) {
            if (row in occupiedRows) continue
            for (col in 0 until n) {
                if (!isValidMove(Position(row, col), positions)) continue
                val trial = positions.toMutableList().apply { add(Position(row, col)) }
                if (canSolveFrom(n, trial)) {
                    return Position(row, col)
                }
            }
        }
        return null
    }

    private fun canSolveFrom(n: Int, positions: MutableList<Position>): Boolean {
        val occupiedRows = positions.map { it.row }.toSet()

        fun backtrack(row: Int): Boolean {
            if (row == n) return true
            if (row in occupiedRows) return backtrack(row + 1)

            for (col in 0 until n) {
                if (isValidMove(Position(row, col), positions)) {
                    positions.add(Position(row, col))
                    if (backtrack(row + 1)) return true
                    positions.removeAt(positions.lastIndex)
                }
            }
            return false
        }

        return backtrack(0)
    }


    private fun generateLevel(n: Int): List<Position> {
        val positions = mutableListOf<Position>()

        fun backtrack(row: Int): Boolean {
            if (row == n) return true

            val cols = (0 until n).toMutableList().also { it.shuffle(Random) }
            for (col in cols) {
                if (isValidMove(Position(row, col), positions)) {
                    positions.add(Position(row, col))
                    if (backtrack(row + 1)) return true
                    positions.removeAt(positions.lastIndex)
                }
            }
            return false
        }

        return if (backtrack(0)) positions else emptyList()
    }
}
