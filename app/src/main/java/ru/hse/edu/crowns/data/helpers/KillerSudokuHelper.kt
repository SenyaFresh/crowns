package ru.hse.edu.crowns.data.helpers

import ru.hse.edu.crowns.model.game.Position
import ru.hse.edu.crowns.model.game.sudoku.SudokuCell
import ru.hse.edu.crowns.model.game.sudoku.SudokuGameState
import ru.hse.edu.crowns.model.game.sudoku.SumZone
import kotlin.random.Random

object KillerSudokuHelper {

    private const val SIZE = 9
    private const val BACKTRACK_EMPTY = -1
    private const val MAX_ZONE_SIZE = 4

    var lastGeneratedGrid: Array<IntArray>? = null

    fun generateLevel(startCount: Int, seed: Long = System.currentTimeMillis()): SudokuGameState {
        require(startCount in 0..SIZE * SIZE) { "startCount must be between 0 and 81" }
        val random = Random(seed)

        val solution = Array(SIZE) { IntArray(SIZE) { BACKTRACK_EMPTY } }
        fillGrid(solution, random)

        lastGeneratedGrid = solution.copyOf().map { it.copyOf() }.toTypedArray()

        val zoneBoard = Array(SIZE) { IntArray(SIZE) { -1 } }
        val zones = mutableListOf<MutableList<Position>>()
        val unassigned = ArrayDeque<Position>().apply {
            for (r in 0 until SIZE) for (c in 0 until SIZE) add(Position(r, c))
        }
        val directions = listOf(
            Position(-1, 0), Position(1, 0), Position(0, -1), Position(0, 1)
        )

        while (unassigned.isNotEmpty()) {
            val start = unassigned.random(random).also { unassigned.remove(it) }
            val zoneIndex = zones.size
            val zone = mutableListOf(start)
            zoneBoard[start.row][start.column] = zoneIndex
            val frontier = ArrayDeque<Position>().apply { add(start) }

            while (frontier.isNotEmpty() && zone.size < MAX_ZONE_SIZE) {
                val p = frontier.removeFirst()
                val usedNums = zone.map { solution[it.row][it.column] }.toSet()
                val candidates = directions.map { d -> Position(p.row + d.row, p.column + d.column) }
                    .filter { n ->
                        n.row in 0 until SIZE && n.column in 0 until SIZE
                                && zoneBoard[n.row][n.column] == -1
                                && unassigned.contains(n)
                                && solution[n.row][n.column] !in usedNums
                    }
                if (candidates.isEmpty()) continue
                val next = candidates.random(random)
                zone += next
                zoneBoard[next.row][next.column] = zoneIndex
                unassigned.remove(next)
                frontier.add(next)
            }
            zones += zone
        }

        val sums = zones.map { cage ->
            val sumValue = cage.sumOf { solution[it.row][it.column] }
            SumZone(sumValue, cage)
        }

        val working = solution.map { it.copyOf() }.toTypedArray()
        val positions = (0 until SIZE * SIZE).map { Position(it / SIZE, it % SIZE) }.shuffled(random)
        for (pos in positions) {
            val r = pos.row
            val c = pos.column
            val temp = working[r][c]
            working[r][c] = BACKTRACK_EMPTY
            if (countSolutions(working) != 1) {
                working[r][c] = temp
            }
        }

        val startCells = mutableListOf<SudokuCell>()
        for (r in 0 until SIZE) for (c in 0 until SIZE) {
            if (working[r][c] != BACKTRACK_EMPTY) {
                startCells += SudokuCell(Position(r, c), working[r][c], true)
            }
        }

        return SudokuGameState(startCells, emptyList(), sums)
    }

    private fun fillGrid(grid: Array<IntArray>, random: Random): Boolean {
        for (r in 0 until SIZE) for (c in 0 until SIZE) {
            if (grid[r][c] == BACKTRACK_EMPTY) {
                for (n in (1..SIZE).shuffled(random)) {
                    if (isValid(grid, r, c, n)) {
                        grid[r][c] = n
                        if (fillGrid(grid, random)) return true
                    }
                }
                grid[r][c] = BACKTRACK_EMPTY
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

    private fun countSolutions(grid: Array<IntArray>): Int {
        fun backtrack(): Int {
            for (r in 0 until SIZE) for (c in 0 until SIZE) {
                if (grid[r][c] == BACKTRACK_EMPTY) {
                    var total = 0
                    for (n in 1..SIZE) {
                        if (isValid(grid, r, c, n)) {
                            grid[r][c] = n
                            total += backtrack()
                            if (total > 1) {
                                grid[r][c] = BACKTRACK_EMPTY
                                return total
                            }
                            grid[r][c] = BACKTRACK_EMPTY
                        }
                    }
                    return total
                }
            }
            return 1
        }
        return backtrack()
    }

    fun generateHint(cells: List<SudokuCell>): SudokuCell? {
        (0 until SIZE).shuffled().forEach { row ->
            (0 until SIZE).shuffled().forEach { column ->
                if (cells.none { it.position == Position(row, column) }) {
                    return SudokuCell(Position(row, column), lastGeneratedGrid!![row][column], true)
                }
            }
        }
        return null
    }
}