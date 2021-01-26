/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.dsl.random
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.component.Required
import com.almasb.fxgl.pathfinding.astar.AStarCell
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent
import javafx.util.Duration
import java.util.function.Predicate

/**
 * Randomly moves the entity using AStarComponent.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(AStarMoveComponent::class)
class RandomAStarMoveComponent
@JvmOverloads constructor(

        /**
         * Minimum distance to move.
         * Default 0, means it is possible we don't move at all.
         * The value of 2 would mean we need to move at least 2 cells.
         */
        var minDistance: Int = 0,

        /**
         * Maximum distance to move.
         * Default unlimited.
         * The value of 5 would mean we can move up to 5 cells.
         */
        var maxDistance: Int = Int.MAX_VALUE,

        /**
         * Min delay between us stopping and then moving again.
         */
        var minDelay: Duration = Duration.seconds(1.0),

        /**
         * Max delay between us stopping and then moving again.
         */
        var maxDelay: Duration = Duration.seconds(1.0)

) : Component() {

    private lateinit var astar: AStarMoveComponent

    private val moveTimer = FXGL.newLocalTimer()

    private var delay = Duration.seconds(random(minDelay.toSeconds(), maxDelay.toSeconds()))

    /**
     * Used to filter which cells can be moved to.
     * By default, all walkable cells can be moved to.
     */
    var cellFilter: Predicate<AStarCell> = Predicate { true }

    override fun onAdded() {
        moveTimer.capture()
    }

    private var wasAtDestination = true

    override fun onUpdate(tpf: Double) {
        val isAtDestination = astar.isAtDestination

        if (!wasAtDestination && isAtDestination) {
            delay = Duration.seconds(random(minDelay.toSeconds(), maxDelay.toSeconds()))
            moveTimer.capture()
        }

        wasAtDestination = isAtDestination

        if (!astar.isAtDestination) {
            return
        }

        if (moveTimer.elapsed(delay)) {
            moveToRandomCell()
        }
    }

    private fun moveToRandomCell() {
        astar.currentCell.ifPresent { currentCell ->
            astar.grid
                    .getRandomCell {
                        it.isWalkable && cellFilter.test(it)
                                && currentCell.distance(it) >= minDistance
                                && currentCell.distance(it) <= maxDistance
                    }
                    .ifPresent {
                        astar.moveToCell(it)
                    }
        }
    }
}