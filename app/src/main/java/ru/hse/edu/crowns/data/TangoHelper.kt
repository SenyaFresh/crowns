package ru.hse.edu.crowns.data

import ru.hse.edu.crowns.model.game.Position
import ru.hse.edu.crowns.model.game.tango.TangoCell
import ru.hse.edu.crowns.model.game.tango.TangoCondition
import ru.hse.edu.crowns.model.game.tango.TangoGameState
import kotlin.random.Random

object TangoHelper {

    fun generateLevel(n: Int, startCount: Int): TangoGameState {
        val board = Array(n) { r ->
            BooleanArray(n) { c -> (r + c) % 2 == 0 }
        }

        fun swapRows(r1: Int, r2: Int) {
            for (c in 0 until n) {
                val tmp = board[r1][c]
                board[r1][c] = board[r2][c]
                board[r2][c] = tmp
            }
        }

        fun swapColumns(c1: Int, c2: Int) {
            for (r in 0 until n) {
                val tmp = board[r][c1]
                board[r][c1] = board[r][c2]
                board[r][c2] = tmp
            }
        }

        repeat(10_000) {
            val firstRow = Random.nextInt(n)
            var secondRow = Random.nextInt(n)
            if (firstRow == secondRow) {
                secondRow = (secondRow + 1) % n
            }
            swapRows(firstRow, secondRow)
            for (c in 0 until n) {
                if (!isColumnValid(firstRow, c, board, n) || !isColumnValid(
                        secondRow,
                        c,
                        board,
                        n
                    )
                ) {
                    swapRows(firstRow, secondRow)
                    break
                }
            }

            val firstColumn = Random.nextInt(n)
            var secondColumn = Random.nextInt(n)
            if (firstColumn == secondColumn) {
                secondColumn = (secondColumn + 1) % n
            }
            swapColumns(firstColumn, secondColumn)
            for (r in 0 until n) {
                if (!isRowValid(r, firstColumn, board, n) || !isRowValid(
                        r,
                        secondColumn,
                        board,
                        n
                    )
                ) {
                    swapColumns(firstColumn, secondColumn)
                    break
                }
            }
        }

        val usedPairs = mutableSetOf<Pair<Position, Position>>()
        val conditions = mutableListOf<TangoCondition>()
        val directions = listOf(0 to 1, 1 to 0, 0 to -1, -1 to 0)

        fun findNeighbor(position: Position) = directions
            .map { (rowOffset, columnOffset) ->
                Position(
                    position.row + rowOffset,
                    position.column + columnOffset
                )
            }
            .first { (neighborRow, neighborColumn) ->
                neighborRow in 0 until n && neighborColumn in 0 until n
            }

        while (conditions.size < n * 3 / 2) {
            val row = Random.nextInt(n)
            val column = Random.nextInt(n)
            val pos = Position(row, column)
            val neighbor = findNeighbor(pos)
            if (usedPairs.contains(pos to neighbor) || usedPairs.contains(neighbor to pos)) {
                continue
            }
            usedPairs.add(pos to neighbor)
            conditions.add(
                TangoCondition(
                    pos,
                    neighbor,
                    board[pos.row][pos.column] == board[neighbor.row][neighbor.column]
                )
            )
        }

        val allPos = mutableListOf<Position>()
        for (r in 0 until n) for (c in 0 until n) allPos += Position(r, c)
        allPos.shuffle()
        val startCells = allPos
            .take(startCount)
            .map { pos -> TangoCell(pos, isSun = board[pos.row][pos.column]) }

        return TangoGameState(
            startCells = startCells,
            playerCells = emptyList(),
            conditions = conditions
        )
    }

    fun isValidMove(
        move: TangoCell,
        cells: List<TangoCell>,
        conditions: List<TangoCondition>,
        n: Int
    ): Boolean {
        conditions.firstOrNull { it.firstPosition == move.position }
            ?.let { condition ->
                cells.firstOrNull { condition.secondPosition == it.position }
                    ?.let {
                        if ((it.isSun == move.isSun) != condition.equal) {
                            return false
                        }
                    }
            }

        conditions.firstOrNull { it.secondPosition == move.position }
            ?.let { condition ->
                cells.firstOrNull { condition.firstPosition == it.position }
                    ?.let {
                        if ((it.isSun == move.isSun) != condition.equal) {
                            return false
                        }
                    }
            }

        val c = move.position.column
        val r = move.position.row
        for (start in listOf(c - 2, c - 1, c)) {
            if (cells.firstOrNull { it.position.column == start && it.position.row == r }?.isSun == move.isSun &&
                cells.firstOrNull { it.position.column == start + 1 && it.position.row == r }?.isSun == move.isSun &&
                cells.firstOrNull { it.position.column == start + 2 && it.position.row == r }?.isSun == move.isSun
            ) {
                return false
            }
        }

        for (start in listOf(r - 2, r - 1, r)) {
            if (cells.firstOrNull { it.position.row == start && it.position.column == r }?.isSun == move.isSun &&
                cells.firstOrNull { it.position.row == start + 1 && it.position.column == r }?.isSun == move.isSun &&
                cells.firstOrNull { it.position.row == start + 2 && it.position.column == r }?.isSun == move.isSun
            ) {
                return false
            }
        }

        if (cells.count { it.position.column == c  && it.isSun == move.isSun} > n / 2) return false

        if (cells.count { it.position.row == r  && it.isSun == move.isSun} > n / 2) return false

        return true
    }

    private fun isRowValid(r: Int, c: Int, board: Array<BooleanArray>, n: Int): Boolean {
        val v = board[r][c]
        for (start in listOf(c - 2, c - 1, c)) {
            if (start >= 0 && start + 2 < n) {
                if (board[r][start] == v &&
                    board[r][start + 1] == v &&
                    board[r][start + 2] == v
                ) {
                    return false
                }
            }
        }
        return true
    }

    private fun isColumnValid(r: Int, c: Int, board: Array<BooleanArray>, n: Int): Boolean {
        val v = board[r][c]
        for (start in listOf(r - 2, r - 1, r)) {
            if (start >= 0 && start + 2 < n) {
                if (board[start][c] == v &&
                    board[start + 1][c] == v &&
                    board[start + 2][c] == v
                ) {
                    return false
                }
            }
        }
        return true
    }

}