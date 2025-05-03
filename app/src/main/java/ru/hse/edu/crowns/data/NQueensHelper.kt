package ru.hse.edu.crowns.data

import kotlin.random.Random

object NQueensHelper {

    fun generateLevel(n: Int, startCount: Int): List<Pair<Int, Int>> {
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
                    if (backtrack(row + 1)) {
                        return true
                    }
                    positions.removeAt(positions.lastIndex)
                }
            }
            return false
        }
        val found = backtrack(0)
        return if (found) positions else emptyList()
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

}