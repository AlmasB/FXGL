/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.extra.entity.components

import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.component.Component

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FollowComponent(var target: Entity? = null) : Component() {

    var speed = 120.0

    var minDistance = 50.0
    var maxDistance = 100.0

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