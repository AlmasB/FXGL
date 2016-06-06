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

import java.util.*

/**
 * API INCOMPLETE
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class MapGenerator(val seed: Long) {

    private val rand = Random(seed)

    constructor() : this(Random().longs().findAny().asLong) { }

    private val ATTRACT_RANGE = 25

    fun create(width: Int, height: Int, pairs: List<Pair<TileType, Int> >): Grid {
        val grid = Grid(width, height)

        grid.rows.forEach {
            it.tiles.forEach {
                it.type = TileType.WATER
            }
        }

        var count = pairs[1].second

        while (count > 0) {
            var x = rand.nextInt(width)
            var y = rand.nextInt(height)

            val startX = x - ATTRACT_RANGE
            val startY = y - ATTRACT_RANGE

            val point = startX.until(startX + 2*ATTRACT_RANGE)
                .flatMap { x1 -> startY.until(startY + 2*ATTRACT_RANGE).map { Pair(x1, it) } }
                .filter { it.first in 0..width-1 && it.second in 0..height-1 && grid.getTile(it.first, it.second).type == TileType.EARTH}
                .firstOrNull()

            if (point != null) {
                x = point.first + rand.nextInt(3) - 1
                y = point.second + rand.nextInt(3) - 1

                while (!grid.isValid(x, y)) {
                    x = point.first + rand.nextInt(3) - 1
                    y = point.second + rand.nextInt(3) - 1
                }
            }

            val tile = grid.getTile(x, y)
            tile.type = TileType.EARTH

            grid.getNeighbors(x, y).forEach {
                it.type = TileType.EARTH
                count--
            }

            count--
        }

        return grid
    }

}

//fun main(args: Array<String>) {
//    val gen = MapGenerator(1000)
//
//    val grid = gen.create(10, 10, listOf(
//            Pair(TileType.WATER, 79),
//            Pair(TileType.EARTH, 21)
//    ))
//
//    println(grid)
//}