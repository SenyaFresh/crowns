package ru.hse.edu.crowns.data

import kotlin.random.Random

object NQueensHelper {

    fun generateColoredLevel(n: Int): List<List<Int>> {
        val positions = generateLevel(n)
        if (positions.isEmpty()) return List(n) { List(n) { -1 } }

        val board = MutableList(n) { MutableList(n) { -1 } }
        val territoryFrontiers = MutableList(n) { ArrayDeque<Pair<Int, Int>>() }
        val directions = listOf(0 to 1, 1 to 0, 0 to -1, -1 to 0)
        val random = Random

        for ((color, pos) in positions.withIndex()) {
            val (r, c) = pos
            board[r][c] = color
            territoryFrontiers[color].addLast(r to c)
        }

        fun freeNeighbors(r: Int, c: Int) = directions
            .map { (dr, dc) -> r + dr to c + dc }
            .filter { (nr, nc) ->
                nr in 0 until n && nc in 0 until n && board[nr][nc] == -1
            }

        var unfilled = n * n - n

        while (unfilled > 0) {
            val activeColors = territoryFrontiers
                .mapIndexedNotNull { idx, q ->
                    if (q.any { freeNeighbors(it.first, it.second).isNotEmpty() }) idx else null
                }
                .shuffled(random)

            for (color in activeColors) {
                if (unfilled == 0) break

                val queue = territoryFrontiers[color]
                repeat(queue.size) {
                    val (r, c) = queue.removeFirst()
                    val neigh = freeNeighbors(r, c)
                    if (neigh.isNotEmpty()) {
                        val (nr, nc) = neigh.random(random)
                        board[nr][nc] = color
                        queue.addLast(nr to nc)
                        if (freeNeighbors(r, c).isNotEmpty()) {
                            queue.addLast(r to c)
                        }
                        unfilled--
                        return@repeat
                    }
                }
            }
        }

        return board
    }


    fun generateDefaultLevel(n: Int, startCount: Int): List<Pair<Int, Int>> {
        return generateLevel(n).shuffled().take(startCount)
    }

    private fun generateLevel(n: Int): List<Pair<Int, Int>> {
        val positions = mutableListOf<Pair<Int, Int>>()

        fun backtrack(row: Int): Boolean {
            if (row == n) return true

            val cols = (0 until n).toMutableList().also { it.shuffle(Random) }
            for (col in cols) {
                if (isValidMove(row, col, positions)) {
                    positions.add(row to col)
                    if (backtrack(row + 1)) return true
                    positions.removeAt(positions.lastIndex)
                }
            }
            return false
        }

        return if (backtrack(0)) positions else emptyList()
    }

    fun isValidMove(row: Int, col: Int, positions: List<Pair<Int, Int>>): Boolean {
        for ((r, c) in positions) {
            if (c == col) return false
            if (r == row) return false
            if (r - c == row - col) return false
            if (r + c == row + col) return false
        }
        return true
    }

    fun generateHint(n: Int, positions: List<Pair<Int, Int>>): Pair<Int, Int>? {
        val occupiedRows = positions.map { it.first }.toSet()
        for (row in 0 until n) {
            if (row in occupiedRows) continue
            for (col in 0 until n) {
                if (!isValidMove(row, col, positions)) continue
                val trial = positions.toMutableList().apply { add(row to col) }
                if (canSolveFrom(n, trial)) {
                    return row to col
                }
            }
        }
        return null
    }

    private fun canSolveFrom(n: Int, positions: MutableList<Pair<Int, Int>>): Boolean {
        val occupiedRows = positions.map { it.first }.toSet()

        fun backtrack(row: Int): Boolean {
            if (row == n) return true
            if (row in occupiedRows) return backtrack(row + 1)

            for (col in 0 until n) {
                if (isValidMove(row, col, positions)) {
                    positions.add(row to col)
                    if (backtrack(row + 1)) return true
                    positions.removeAt(positions.lastIndex)
                }
            }
            return false
        }

        return backtrack(0)
    }
}
