/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.time.LocalTimer
import javafx.util.Duration

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FollowComponent(
        var target: Entity? = null,
        var speed: Double = 120.0,
        var minDistance: Double = 50.0,
        var maxDistance: Double = 100.0
) : Component() {

    private val timer: LocalTimer = FXGL.newLocalTimer()
    private var moveDelay: Duration = Duration.ZERO

    fun setMoveDelay(value: Duration): FollowComponent {
        moveDelay = value
        return this
    }

    override fun onUpdate(tpf: Double) {
        if (!canMove())
            return

        target?.let {
            when {
                (entity.distance(it) < minDistance) -> {
                    val oppositeVector = entity.center.subtract(it.center).normalize().multiply(speed * tpf)
                    entity.translate(oppositeVector)
                }

                (entity.distance(it) > maxDistance) -> {
                    entity.translateTowards(it.center, speed * tpf)
                }

                // not moving, so capture time before we can move again
                else -> {
                    timer.capture()
                }
            }
        }
    }

    private fun canMove(): Boolean {
        return timer.elapsed(moveDelay)
    }
}