/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.minigames.circuitbreaker

import java.util.*


/**
 * A 2d maze.
 *
 * Slightly modified and adapted version from
 * [Rosetta Code](http://rosettacode.org/wiki/Maze_generation#Java).
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class Maze
/**
 * Constructs a new maze with given width and height.
 *
 * @param width maze width
 * @param height maze height
 */
(val width: Int, val height: Int) {

    private val maze: Array<IntArray> = Array(width) { IntArray(height) }
    private val mazeCells: Array<Array<MazeCell>> = Array(width) { Array(height) { MazeCell(0, 0) } }

    fun getMazeCell(x: Int, y: Int): MazeCell {
        return mazeCells[x][y]
    }

    init {
        generateMaze(0, 0)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val cell = MazeCell(x, y)

                if (maze[x][y] and 1 == 0)
                    cell.hasTopWall = true

                if (maze[x][y] and 8 == 0)
                    cell.hasLeftWall = true

                mazeCells[x][y] = cell
            }
        }
    }

    private fun generateMaze(cx: Int, cy: Int) {
        val dirs = DIR.values().toList().shuffled()

        for (dir in dirs) {
            val nx = cx + dir.dx
            val ny = cy + dir.dy
            if (nx in 0 until width && ny in 0 until height
                    && maze[nx][ny] == 0) {
                maze[cx][cy] = maze[cx][cy] or dir.bit
                maze[nx][ny] = maze[nx][ny] or dir.opposite!!.bit
                generateMaze(nx, ny)
            }
        }
    }

    private enum class DIR constructor(
            val bit: Int,
            val dx: Int,
            val dy: Int) {

        N(1, 0, -1),
        S(2, 0, 1),
        E(4, 1, 0),
        W(8, -1, 0);

        var opposite: DIR? = null

        companion object {

            init {
                N.opposite = S
                S.opposite = N
                E.opposite = W
                W.opposite = E
            }
        }
    }
}

/**
 * Represents a single cell in a maze.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class MazeCell internal constructor(
        /**
         * @return x coordinate of this cell in the grid
         */
        val x: Int,
        /**
         * @return y coordinate of this cell in the grid
         */
        val y: Int) {

    internal var hasTopWall = false
    internal var hasLeftWall = false
}