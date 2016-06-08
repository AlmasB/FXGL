/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.algorithm

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Grid(val width: Int, val height: Int) {

    val rows = Array<Row>(height, { i -> Row(width) })
    val columns = Array<Column>(width, { i -> Column(height) })

    class Row(val width: Int) {
        val tiles = Array<Tile>(width, { i -> Tile() } )
    }

    class Column(val height: Int) {
        val tiles = Array<Tile>(height, { i -> Tile() } )
    }

    fun isValid(x: Int, y: Int) = x in 0..width-1 && y in 0..height-1

    fun getTile(x: Int, y: Int) = rows[y].tiles[x]

    fun getNeighbors(x: Int, y: Int): List<Tile> {
        val points = listOf(
                Pair(-1, 0),
                Pair(1, 0),
                Pair(0, 1),
                Pair(0, -1)
        )

        return points
                .map { Pair(it.first + x, it.second + y) }
                .filter { it.first in 0..width-1 && it.second in 0..height-1 }
                .map { getTile(it.first, it.second) }
    }

    override fun toString(): String {
        return rows.map { it.tiles.map { it.type.toString() }.fold("", {a, b -> a + b}) }
            .fold("", { row1, row2 -> row1 + "\n" + row2 })
    }
}