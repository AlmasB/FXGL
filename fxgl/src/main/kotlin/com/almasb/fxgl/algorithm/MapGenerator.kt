/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
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