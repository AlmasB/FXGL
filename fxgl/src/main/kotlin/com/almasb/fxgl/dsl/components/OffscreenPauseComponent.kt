/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.component.ComponentListener

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class OffscreenPauseComponent : AccumulatedUpdateComponent(3) {
    private val viewport = FXGL.getGameScene().viewport

    private val components = arrayListOf<Component>()

    private val listener = object : ComponentListener {
        override fun onAdded(component: Component) {
            if (component !== this@OffscreenPauseComponent) {
                components += component
            }
        }

        override fun onRemoved(component: Component) {
            components -= component
        }
    }

    override fun onAdded() {
        components += entity.components

        entity.addComponentListener(listener)
    }

    override fun onAccumulatedUpdate(tpfSum: Double) {
        if (entity.boundingBoxComponent.isOutside(viewport.visibleArea)) {
            components.forEach { it.pause() }
        } else {
            components.forEach { it.resume() }
        }
    }

    override fun onRemoved() {
        entity.removeComponentListener(listener)

        components.clear()
    }
}