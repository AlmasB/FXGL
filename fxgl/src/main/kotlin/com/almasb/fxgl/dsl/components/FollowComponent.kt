/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.component.Component

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FollowComponent(
        var target: Entity? = null,
        var speed: Double = 120.0,
        var minDistance: Double = 50.0,
        var maxDistance: Double = 100.0
) : Component() {

    override fun onUpdate(tpf: Double) {
        target?.let {
            if (entity.distance(it) < minDistance) {

                val oppositeVector = entity.position.subtract(it.position).normalize().multiply(speed * tpf)

                entity.translate(oppositeVector)

            } else if (entity.distance(it) > maxDistance) {
                entity.translateTowards(it.center, speed * tpf)
            }
        }
    }
}