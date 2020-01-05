/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.app.Viewport
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.entity.component.Component

/**
 * A component that keeps an entity within the viewport.
 * Entities with physics enabled are not supported.
 * Do NOT use this component if viewport is bound to an entity.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class KeepOnScreenComponent : Component() {

    private lateinit var viewport: Viewport

    /**
     * keep on screen in X axis.
     */
    var isHorizontal = true

    /**
     * keep on screen in Y axis.
     */
    var isVertical = true

    override fun onAdded() {
        viewport = FXGL.getGameScene().viewport
    }

    override fun onUpdate(tpf: Double) {
        blockWithBBox()
    }

    private fun blockWithBBox() {
        if (isHorizontal) {
            if (getEntity().x < viewport.x) {
                getEntity().x = viewport.x
            } else if (getEntity().rightX > viewport.x + viewport.width) {
                getEntity().x = viewport.x + viewport.width - getEntity().width
            }
        }

        if (isVertical) {
            if (getEntity().y < viewport.y) {
                getEntity().y = viewport.y
            } else if (getEntity().bottomY > viewport.y + viewport.height) {
                getEntity().y = viewport.y + viewport.height - getEntity().height
            }
        }
    }

    fun onlyHorizontally() = this.apply {
        isVertical = false
        isHorizontal = true
    }

    fun onlyVertically() = this.apply {
        isVertical = true
        isHorizontal = false
    }

    fun bothAxes() = this.apply {
        isVertical = true
        isHorizontal = true
    }
}