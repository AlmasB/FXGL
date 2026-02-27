/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.dfs

import com.almasb.fxgl.core.collection.grid.NeighborDirection
import com.almasb.fxgl.core.collection.grid.NeighborSelectionStrategy
import com.almasb.fxgl.pathfinding.Pathfinder
import com.almasb.fxgl.pathfinding.astar.AStarCell
import com.almasb.fxgl.pathfinding.TraversableGrid
import java.util.*

/**
 * TODO: incomplete impl
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
class DFSPathfinder<T : AStarCell>(grid: TraversableGrid<T>) : Pathfinder<T>(grid) {

    override fun findPath(
        sourceX: Int,
        sourceY: Int,
        targetX: Int,
        targetY: Int,
        neighborDirection: NeighborDirection,
        busyCells: MutableList<T>
    ): MutableList<T> {

        grid.cells.forEach { it.parent = null }

        val open = arrayListOf<T>()
        val closed = arrayListOf<T>()

        val start = grid.get(sourceX, sourceY)
        val target = grid.get(targetX, targetY)

        var current = start

        closed.add(current)

        // TODO: target not ....

        var isFound = false

        while (!isFound && !closed.contains(target)) {
            val neighbors = getValidNeighbors(current, neighborDirection, busyCells)

            var isDeadEnd = true

            for (neighbor in neighbors) {
                cellVisitListener.onVisit(neighbor)

                if (neighbor == target) {
                    target.setParent(current)
                    isFound = true
                    closed.add(target)
                    break
                }

                if (neighbor !in open && neighbor !in closed) {
                    open += neighbor

                    if (neighbor.parent == null) {
                        neighbor.parent = current
                    }

                    isDeadEnd = false
                }
            }

            if (isFound) {
                break
            }

            if (isDeadEnd) {
                closed.add(current)
                open.remove(current)
            }

            if (open.isNotEmpty()) {
                val neighbor = open.last()

                current = neighbor

            } else {
                println("no path")

                return Collections.emptyList()
            }
        }

        println("building path")

        val path = buildPath(start, target)
        val result = ArrayList<T>(path)

        pathFoundListener.onPathFound(result)

        return result
    }

    private fun buildPath(start: T, target: T): List<T?> {
        val path: MutableList<T?> = ArrayList()

        var tmp = target
        do {
            path.add(tmp)
            tmp = tmp.parent as T
        } while (tmp !== start)

        Collections.reverse(path)
        return path
    }

    private fun getValidNeighbors(node: T, neighborDirection: NeighborDirection, busyNodes: List<AStarCell>): List<T> {
        val result = ArrayList(grid.getNeighbors(node.x, node.y, NeighborSelectionStrategy.LEFT_UP_RIGHT_DOWN))
        result.removeAll(busyNodes)
        result.removeIf { cell: T -> !grid.isTraversableInSingleMove(node, cell) }
        return result
    }
}