/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.extra.entity.components

import com.almasb.fxgl.entity.component.Component

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AttractorComponent(var force: Double,
                         var radius: Double) : Component() {

    override fun onUpdate(tpf: Double) {

        // https://github.com/AlmasB/FXGL/issues/482
        entity.world
                .getEntitiesByComponent<AttractableComponent>()
                .filter { it.distance(entity) <= radius }
                .forEach {
                    val finalForce = force - it.getComponent(AttractableComponent::class.java).value
                    it.translateTowards(entity.position, finalForce * tpf)
                }
    }
}