package ru.hse.edu.crowns.data

import ru.hse.edu.crowns.model.game.Position
import ru.hse.edu.crowns.model.game.sudoku.SudokuCell
import ru.hse.edu.crowns.model.game.sudoku.SudokuGameState
import kotlin.random.Random

object KillerSudokuHelper {

    private const val SIZE = 9
    private const val EMPTY = 0
    private const val ZONE_COUNT = 30

    fun generateLevel(startCount: Int): SudokuGameState {
        val random = Random

        val solution = Array(SIZE) { IntArray(SIZE) { EMPTY } }
        fillGrid(solution, random)

        val zoneBoard = Array(SIZE) { IntArray(SIZE) { -1 } }
        val territoryFrontiers = List(ZONE_COUNT) { ArrayDeque<Position>() }
        val allPositions = mutableListOf<Position>()
        for (r in 0 until SIZE) for (c in 0 until SIZE) allPositions += Position(r, c)
        allPositions.shuffle(random)
        for (zone in 0 until ZONE_COUNT) {
            val pos = allPositions.removeAt(0)
            zoneBoard[pos.row][pos.column] = zone
            territoryFrontiers[zone].add(pos)
        }
        var unfilled = SIZE * SIZE - ZONE_COUNT

        fun freeNeighbors(p: Position): List<Position> {
            return listOf(
                Position(p.row - 1, p.column),
                Position(p.row + 1, p.column),
                Position(p.row, p.column - 1),
                Position(p.row, p.column + 1)
            ).filter { it.row in 0 until SIZE && it.column in 0 until SIZE && zoneBoard[it.row][it.column] == -1 }
        }

        while (unfilled > 0) {
            val active = territoryFrontiers.mapIndexedNotNull { idx, dq ->
                    if (dq.any { freeNeighbors(it).isNotEmpty() }) idx else null
                }.shuffled(random)
            for (zone in active) {
                if (unfilled == 0) break
                val dq = territoryFrontiers[zone]
                repeat(dq.size) {
                    val p = dq.removeFirst()
                    val neigh = freeNeighbors(p)
                    if (neigh.isNotEmpty()) {
                        val nb = neigh.random(random)
                        zoneBoard[nb.row][nb.column] = zone
                        dq.addLast(nb)
                        if (freeNeighbors(p).isNotEmpty()) {
                            dq.addLast(p)
                        }
                        unfilled--
                        return@repeat
                    }
                }
            }
        }

        val zones = MutableList(ZONE_COUNT) { mutableListOf<Position>() }
        for (r in 0 until SIZE) for (c in 0 until SIZE) {
            zones[zoneBoard[r][c]].add(Position(r, c))
        }
        val sumsMap = zones.map { cage ->
                val sum = cage.sumOf { solution[it.row][it.column] }
                val flat = cage.map { it.row * SIZE + it.column }
                sum to flat
            }.groupBy({ it.first }, { it.second })

        val coords = (0 until SIZE * SIZE).toMutableList().shuffled(random)
        val openSet = coords.take(startCount).toSet()
        val startCells = mutableListOf<SudokuCell>()
        val playerCells = mutableListOf<SudokuCell>()
        for (r in 0 until SIZE) for (c in 0 until SIZE) {
            val pos = Position(r, c)
            val idx = r * SIZE + c
            if (idx in openSet) {
                startCells += SudokuCell(pos, solution[r][c], true)
            } else {
                playerCells += SudokuCell(pos, EMPTY, true)
            }
        }

        return SudokuGameState(startCells, playerCells, sumsMap)
    }

    private fun fillGrid(grid: Array<IntArray>, random: Random): Boolean {
        for (r in 0 until SIZE) for (c in 0 until SIZE) {
            if (grid[r][c] == EMPTY) {
                val nums = (1..SIZE).shuffled(random)
                for (n in nums) {
                    if (isValid(grid, r, c, n)) {
                        grid[r][c] = n
                        if (fillGrid(grid, random)) return true
                    }
                }
                grid[r][c] = EMPTY
                return false
            }
        }
        return true
    }

    private fun isValid(grid: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        for (i in 0 until SIZE) {
            if (grid[row][i] == num || grid[i][col] == num) return false
        }
        val br = row / 3 * 3
        val bc = col / 3 * 3
        for (r in br until br + 3) for (c in bc until bc + 3) {
            if (grid[r][c] == num) return false
        }
        return true
    }
}