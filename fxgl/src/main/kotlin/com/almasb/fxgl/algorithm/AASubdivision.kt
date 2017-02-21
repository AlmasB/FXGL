/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

import com.almasb.fxgl.core.collection.Array
import com.almasb.fxgl.core.math.FXGLMath.random
import com.almasb.fxgl.core.math.FXGLMath.randomBoolean
import javafx.geometry.Rectangle2D

/**
 * Axis-aligned 2D space subdivision.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object AASubdivision {

    /**
     * Subdivides given 2D space ([rect]) into maximum of n = [maxSubspaces] such that
     * each subspace has width and height no less than [minSize].
     */
    @JvmStatic fun divide(rect: Rectangle2D, maxSubspaces: Int, minSize: Int): Array<Rectangle2D> {

        // keeps currently being processed subspaces
        val grids = arrayListOf<Rectangle2D>(rect)

        val result = Array<Rectangle2D>(maxSubspaces)

        for (i in 1..maxSubspaces -1) {
            var grid: Rectangle2D
            var divisible: Pair<Boolean, Boolean>

            do {
                if (grids.isEmpty())
                    throw RuntimeException("Cannot subdivide")

                grid = grids[random(grids.size-1)]
                grids.remove(grid)

                divisible = isDivisible(grid, minSize)

            } while (!divisible.first && !divisible.second)

            // grid is about to be subdivided, so remove from result list
            result.removeValue(grid, true)

            var pair: Pair<Rectangle2D, Rectangle2D>

            // we know at least 1 side is divisible
            // if both are valid, then flip a coin
            if (divisible.first && divisible.second) {
                pair = if (randomBoolean()) subdivideHorizontal(grid, minSize) else subdivideVertical(grid, minSize)
            } else if (divisible.first) {
                // only horizontally divisible
                pair = subdivideHorizontal(grid, minSize)
            } else {
                // only vertically divisible
                pair = subdivideVertical(grid, minSize)
            }

            // push divided items to tmp and result list
            grids.add(pair.first)
            grids.add(pair.second)

            result.addAll(pair.first, pair.second)
        }

        return result
    }

    private fun isDivisible(grid: Rectangle2D, minSize: Int): Pair<Boolean, Boolean> {
        val horizontal = grid.width / 2 >= minSize
        val vertical = grid.height / 2 >= minSize

        return horizontal.to(vertical)
    }

    private fun subdivideVertical(grid: Rectangle2D, minSize: Int): Pair<Rectangle2D, Rectangle2D> {
        val lineY = random(grid.minY.toInt() + minSize, grid.maxY.toInt() - minSize).toDouble()

        return Rectangle2D(grid.minX, grid.minY, grid.width, lineY - grid.minY)
                .to(Rectangle2D(grid.minX, lineY, grid.width, grid.maxY - lineY))
    }

    private fun subdivideHorizontal(grid: Rectangle2D, minSize: Int): Pair<Rectangle2D, Rectangle2D> {
        val lineX = random(grid.minX.toInt() + minSize, grid.maxX.toInt() - minSize).toDouble()

        return Rectangle2D(grid.minX, grid.minY, lineX - grid.minX, grid.height)
                .to(Rectangle2D(lineX, grid.minY, grid.maxX - lineX, grid.height))
    }
}