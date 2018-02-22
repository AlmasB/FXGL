/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.control

import com.almasb.fxgl.entity.Control
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.component.AttractableComponent

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AttractorControl(var force: Double,
                       var radius: Double) : Control() {

    override fun onUpdate(entity: Entity, tpf: Double) {

        // https://github.com/AlmasB/FXGL/issues/482
        entity.world
                .getEntitiesByComponent(AttractableComponent::class.java)
                .filter { it.distance(entity) <= radius }
                .forEach {
                    val finalForce = force - it.getComponent(AttractableComponent::class.java).value
                    it.translateTowards(entity.position, finalForce * tpf)
                }
    }
}