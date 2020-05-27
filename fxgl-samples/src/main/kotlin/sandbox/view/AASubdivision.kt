/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.view

import com.almasb.fxgl.core.collection.Array
import com.almasb.fxgl.core.math.FXGLMath
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

                grid = grids[random(0, grids.size-1)]
                grids.remove(grid)

                divisible = isDivisible(grid, minSize)

            } while (!divisible.first && !divisible.second)

            // grid is about to be subdivided, so remove from result list
            result.removeValueByIdentity(grid)

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