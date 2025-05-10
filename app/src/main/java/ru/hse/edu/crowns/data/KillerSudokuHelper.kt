package ru.hse.edu.crowns.data

import ru.hse.edu.crowns.model.game.Position
import ru.hse.edu.crowns.model.game.sudoku.SudokuCell
import ru.hse.edu.crowns.model.game.sudoku.SudokuGameState
import kotlin.random.Random

object KillerSudokuHelper {

    private const val SIZE = 9
    private const val BACKTRACK_EMPTY = -1 // для заполнения решения
    private const val MAX_ZONE_SIZE = 4

    private var lastGeneratedGrid: Array<IntArray>? = null

    fun generateLevel(startCount: Int, seed: Long = System.currentTimeMillis()): SudokuGameState {
        val random = Random(seed)

        // 1) Сгенерировать полное решение классического судоку
        val solution = Array(SIZE) { IntArray(SIZE) { BACKTRACK_EMPTY } }
        fillGrid(solution, random)

        lastGeneratedGrid = solution.copyOf().map { it.copyOf() }.toTypedArray()

        // 2) Разбить поле на зоны (каджи) с max размером 4
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
                val candidates =
                    directions.map { d -> Position(p.row + d.row, p.column + d.column) }
                        .filter { n ->
                            n.row in 0 until SIZE && n.column in 0 until SIZE
                                    && zoneBoard[n.row][n.column] == -1
                                    && unassigned.contains(n)
                                    && solution[n.row][n.column] !in usedNums
                        }
                if (candidates.isEmpty()) continue
                val next = candidates.random(random)
                zone.add(next)
                zoneBoard[next.row][next.column] = zoneIndex
                unassigned.remove(next)
                frontier.add(next)
            }
            zones += zone
        }

        // 3) Построить SumsMap: сумма -> список зон (плоских индексов)
        val sumsMap = zones
            .map { cage ->
                val sum = cage.sumOf { solution[it.row][it.column] }
                val flat = cage.map { it.row * SIZE + it.column }
                sum to flat
            }
            .groupBy({ it.first }, { it.second })

        // 4) Выбрать startCount предзаполненных ячеек (остальные не возвращаем)
        val indices = (0 until SIZE * SIZE).shuffled(random)
        val openSet = indices.take(startCount).toSet()
        val startCells = mutableListOf<SudokuCell>()
        for (r in 0 until SIZE) for (c in 0 until SIZE) {
            val idx = r * SIZE + c
            if (idx in openSet) {
                startCells += SudokuCell(Position(r, c), solution[r][c], true)
            }
        }

        // Не возвращаем пустые ячейки, список playerCells остаётся пустым
        return SudokuGameState(startCells, emptyList(), sumsMap)
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