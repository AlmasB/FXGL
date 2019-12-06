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
import java.time.LocalTime

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FollowComponent(
        var target: Entity? = null,
        var speed: Double = 120.0,
        var minDistance: Double = 50.0,
        var maxDistance: Double = 100.0
) : Component() {
    private var timer: LocalTimer = FXGL.newLocalTimer()
    private var moveDelay: Duration = Duration.ZERO

    fun setMoveDelay(value: Duration): FollowComponent {
        moveDelay = value
        return this
    }

    private fun canMove(): Boolean{
        return timer.elapsed(moveDelay)
    }

    override fun onUpdate(tpf: Double) {
        target?.let {
            when {
                entity.distance(it) < minDistance -> {
                    if (!canMove())
                        return

                    val oppositeVector = entity.position.subtract(it.position).normalize().multiply(speed * tpf)
                    entity.translate(oppositeVector)
                }
                entity.distance(it) > maxDistance -> {
                    if (!canMove())
                        return

                    entity.translateTowards(it.center, speed * tpf)
                }
                canMove() -> timer.capture()
            }
        }
    }
}