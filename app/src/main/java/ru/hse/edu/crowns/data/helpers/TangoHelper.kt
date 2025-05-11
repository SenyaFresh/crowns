package ru.hse.edu.crowns.data.helpers

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
            .shuffled()
            .map { (rowOffset, columnOffset) ->
                Position(
                    position.row + rowOffset,
                    position.column + columnOffset
                )
            }
            .first { (neighborRow, neighborColumn) ->
                neighborRow in 0 until n && neighborColumn in 0 until n
            }

        while (conditions.size < n * 2.5) {
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
            if (cells.firstOrNull { it.position.row == start && it.position.column == c }?.isSun == move.isSun &&
                cells.firstOrNull { it.position.row == start + 1 && it.position.column == c }?.isSun == move.isSun &&
                cells.firstOrNull { it.position.row == start + 2 && it.position.column == c }?.isSun == move.isSun
            ) {
                return false
            }
        }

        if (cells.count { it.position.column == c && it.isSun == move.isSun } > n / 2) return false

        if (cells.count { it.position.row == r && it.isSun == move.isSun } > n / 2) return false

        return true
    }

    fun generateHint(n: Int, cells: List<TangoCell>, conditions: List<TangoCondition>): TangoCell? {

        val directions = listOf(0 to 1, 1 to 0)

        fun findSameNeighbors(position: Position, isSun: Boolean): List<TangoCell> {
            val neighbors = directions.map { (rowOffset, columnOffset) ->
                Position(
                    position.row + rowOffset,
                    position.column + columnOffset
                )
            }
                .filter { (neighborRow, neighborColumn) ->
                    neighborRow in 0 until n && neighborColumn in 0 until n
                }
            return cells.filter { cell ->
                neighbors.any {
                    it.row == cell.position.row
                            && it.column == cell.position.column
                            && cell.isSun == isSun
                }
            }
        }

        cells.forEach { cell ->
            val neighbors = findSameNeighbors(cell.position, cell.isSun)
            neighbors.forEach { neighbor ->
                if (cell.position.row == neighbor.position.row) {
                    val leftCol = cell.position.column - 1
                    val rightCol = neighbor.position.column + 1
                    if (leftCol > 0
                        && !cells.any { it.position == Position(cell.position.row, leftCol) }
                        && isValidMove(
                            TangoCell(Position(cell.position.row, leftCol), !cell.isSun),
                            cells,
                            conditions,
                            n
                        )
                    ) {
                        return TangoCell(Position(cell.position.row, leftCol), !cell.isSun)
                    }
                    if (rightCol < n
                        && !cells.any { it.position == Position(cell.position.row, rightCol) }
                        && isValidMove(
                            TangoCell(Position(cell.position.row, rightCol), !cell.isSun),
                            cells,
                            conditions,
                            n
                        )
                    ) {
                        return TangoCell(Position(cell.position.row, rightCol), !cell.isSun)
                    }
                } else {
                    val topRow = cell.position.row - 1
                    val bottomRow = neighbor.position.row + 1
                    if (topRow > 0
                        && !cells.any { it.position == Position(topRow, cell.position.column) }
                        && isValidMove(
                            TangoCell(Position(topRow, cell.position.column), !cell.isSun),
                            cells,
                            conditions,
                            n
                        )
                    ) {
                        return TangoCell(Position(topRow, cell.position.column), !cell.isSun)
                    }
                    if (bottomRow < n
                        && !cells.any { it.position == Position(bottomRow, cell.position.column) }
                        && isValidMove(
                            TangoCell(Position(bottomRow, cell.position.column), !cell.isSun),
                            cells,
                            conditions,
                            n
                        )
                    ) {
                        return TangoCell(Position(bottomRow, cell.position.column), !cell.isSun)
                    }
                }
            }
        }

        (0 until n).forEach { row ->
            if (cells.count { it.position.row == row && it.isSun } == n / 2) {
                (0 until n).forEach { column ->
                    if (!cells.any { it.position.row == row && it.position.column == column }) {
                        val hint = TangoCell(Position(row, column), false)
                        if (isValidMove(hint, cells, conditions, n)) {
                            return hint
                        }
                    }
                }
            }
            if (cells.count { it.position.row == row && !it.isSun } == n / 2) {
                (0 until n).forEach { column ->
                    if (!cells.any { it.position.row == row && it.position.column == column }) {
                        val hint = TangoCell(Position(row, column), true)
                        if (isValidMove(hint, cells, conditions, n)) {
                            return hint
                        }
                    }
                }
            }
        }

        (0 until n).forEach { column ->
            if (cells.count { it.position.column == column && it.isSun } == n / 2) {
                (0 until n).forEach { row ->
                    if (!cells.any { it.position.row == row && it.position.column == column }) {
                        val hint = TangoCell(Position(row, column), false)
                        if (isValidMove(hint, cells, conditions, n)) {
                            return hint
                        }
                    }
                }
            }
            if (cells.count { it.position.column == column && !it.isSun } == n / 2) {
                (0 until n).forEach { row ->
                    if (!cells.any { it.position.row == row && it.position.column == column }) {
                        val hint = TangoCell(Position(row, column), true)
                        if (isValidMove(hint, cells, conditions, n)) {
                            return hint
                        }
                    }
                }
            }
        }

        conditions.forEach { condition ->
            val first = cells.firstOrNull { it.position == condition.firstPosition }
            val second = cells.firstOrNull { it.position == condition.secondPosition }
            if (first != null && second != null) return@forEach
            if (first != null) {
                return TangoCell(
                    condition.secondPosition,
                    if (condition.equal) first.isSun else !first.isSun
                )
            } else if (second != null) {
                return TangoCell(
                    condition.firstPosition,
                    if (condition.equal) second.isSun else !second.isSun
                )
            }
        }

        (0 until n).forEach { row ->
            (0 until n).forEach { column ->
                if (!cells.any { it.position.row == row && it.position.column == column }) {
                    var hint = TangoCell(Position(row, column), false)
                    if (isValidMove(hint, cells, conditions, n)) {
                        return hint
                    }
                    hint = hint.copy(isSun = true)
                    if (isValidMove(hint, cells, conditions, n)) {
                        return hint
                    }
                }
            }
        }

        return null
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