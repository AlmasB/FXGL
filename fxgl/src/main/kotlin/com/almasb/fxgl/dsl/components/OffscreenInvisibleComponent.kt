/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.app.scene.Viewport
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.entity.component.Component

/**
 * Hides an entity if it is outside of the visible area of the viewport.
 *
 * @author Johan Dykström
 */
class OffscreenInvisibleComponent
@JvmOverloads constructor(val viewport: Viewport = FXGL.getGameScene().viewport) : Component() {

    override fun onAdded() {
        updateVisibility()
    }

    override fun onUpdate(tpf: Double) {
        updateVisibility()
    }

    private fun updateVisibility() {
        if (entity.boundingBoxComponent.isOutside(viewport.visibleArea)) {
            entity.isVisible = false
        } else if (!entity.isVisible) {
            entity.isVisible = true
        }
    }

    override fun isComponentInjectionRequired(): Boolean = false
}
