/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.component.Component
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAmount

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FollowComponent(
        var target: Entity? = null,
        var speed: Double = 120.0,
        var minDistance: Double = 50.0,
        var maxDistance: Double = 100.0
) : Component() {
    private var moveDelay: Duration = Duration.ZERO
    private var cacheTime: LocalTime = LocalTime.now()

    fun setMoveDelay(value: Duration): FollowComponent {
        moveDelay = value
        return this
    }

    private fun canMove(): Boolean{
        return moveDelay < Duration.between(cacheTime, LocalTime.now())
    }

    override fun onUpdate(tpf: Double) {
        target?.let {
            if (entity.distance(it) < minDistance) {
                if (!canMove())
                    return

                val oppositeVector = entity.position.subtract(it.position).normalize().multiply(speed * tpf)
                entity.translate(oppositeVector)
            } else if (entity.distance(it) > maxDistance) {
                if (!canMove())
                    return

                entity.translateTowards(it.center, speed * tpf)
            } else if (canMove()) {
                cacheTime = LocalTime.now()
            }
        }
    }
}