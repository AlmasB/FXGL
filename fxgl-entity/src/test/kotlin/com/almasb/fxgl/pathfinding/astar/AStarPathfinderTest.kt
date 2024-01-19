/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.pathfinding.astar

import com.almasb.fxgl.core.collection.grid.Diagonal
import com.almasb.fxgl.pathfinding.CellState
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.function.Supplier

class AStarPathfinderTest {
    private lateinit var grid: AStarGrid
    private lateinit var pathfinder: AStarPathfinder

    @BeforeEach
    fun setUp() {
        grid = AStarGrid(GRID_SIZE, GRID_SIZE)
        pathfinder = AStarPathfinder(grid)
    }

    @Test
    fun testFindPath() {
        var path = pathfinder.findPath(3, 0, 5, 0)
        assertPathEquals(path, 4, 0, 5, 0)

        // Add barriers.
        for (i in 0..4) grid[4, i].state = CellState.NOT_WALKABLE
        path = pathfinder.findPath(3, 0, 5, 0)
        assertPathEquals(path,
                3, 1,
                3, 2,
                3, 3,
                3, 4,
                3, 5,
                4, 5,
                5, 5,
                5, 4,
                5, 3,
                5, 2,
                5, 1,
                5, 0)

        // Now test Diagonal Path Finding
        grid.diagonal = Diagonal.ALLOWED
        path = pathfinder.findPath(3, 0, 5, 0)
        assertPathEquals(path,
                3, 1,
                3, 2,
                3, 3,
                3, 4,
                4, 5,
                5, 4,
                5, 3,
                5, 2,
                5, 1,
                5, 0)

        // Make passing impossible.
        for (i in 0..19) grid[4, i].state = CellState.NOT_WALKABLE
        path = pathfinder.findPath(3, 0, 5, 0)
        assertTrue(path.isEmpty())
    }

    @Test
    fun testFindPathWithBusyCells() {
        grid[3, 0].state = CellState.NOT_WALKABLE
        grid[3, 1].state = CellState.NOT_WALKABLE
        grid[3, 2].state = CellState.NOT_WALKABLE
        grid[3, 3].state = CellState.NOT_WALKABLE
        grid[3, 5].state = CellState.NOT_WALKABLE
        grid[1, 4].state = CellState.NOT_WALKABLE
        var path = pathfinder.findPath(1, 1, 4, 5, ArrayList())

        assertThat(path.size, `is`(7))

        var last = path.last()

        assertThat(last.x, `is`(4))
        assertThat(last.y, `is`(5))

        var pathWithBusyCell = pathfinder.findPath(1, 1, 4, 5, listOf(grid[3, 4]))

        assertThat(pathWithBusyCell.size, `is`(9))

        last = pathWithBusyCell.last()

        assertThat(last.x, `is`(4))
        assertThat(last.y, `is`(5))

        // Perform Diagonal Testing
        grid.diagonal = Diagonal.ALLOWED

        path = pathfinder.findPath(1, 1, 4, 5, ArrayList())

        assertThat(path.size, `is`(4))

        last = path.last()

        assertThat(last.x, `is`(4))
        assertThat(last.y, `is`(5))

        pathWithBusyCell = pathfinder.findPath(1, 1, 4, 5, listOf(grid[3, 4]))

        assertThat(pathWithBusyCell.size, `is`(6))

        last = pathWithBusyCell.last()

        assertThat(last.x, `is`(4))
        assertThat(last.y, `is`(5))
    }

    private fun assertPathEquals(path: List<AStarCell>, vararg points: Int) {
        val pointsList = points.toList().chunked(2) { it[0] to it[1] }
        val errorMsg = reportNotMatchingPaths(path, pointsList)
        assertEquals(pointsList.size, path.size, errorMsg)
        pointsList.zip(path).forEach { (point, cell) ->
            assertEquals(point.first, cell.x, errorMsg)
            assertEquals(point.second, cell.y, errorMsg)
        }
    }

    private fun reportNotMatchingPaths(path: List<AStarCell>, points: List<Pair<Int, Int>>): Supplier<String> =
            Supplier { "Paths do not match: \n$path \n!=\n${points}" }

    companion object {
        private const val GRID_SIZE = 20
    }
}