/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.entity.component.Component
import javafx.geometry.Rectangle2D

/**
 * A component that keeps an entity within the viewport.
 * Entities with physics enabled are not supported.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
open class KeepInBoundsComponent(var bounds: Rectangle2D) : Component() {

    /**
     * Keep in bounds in X axis.
     */
    var isHorizontal = true

    /**
     * Keep in bounds in Y axis.
     */
    var isVertical = true

    override fun onUpdate(tpf: Double) {
        blockWithBBox()
    }

    private fun blockWithBBox() {
        if (isHorizontal) {
            if (getEntity().x < bounds.minX) {
                getEntity().x = bounds.minX
            } else if (getEntity().rightX > bounds.maxX) {
                getEntity().x = bounds.maxX - getEntity().width
            }
        }

        if (isVertical) {
            if (getEntity().y < bounds.minY) {
                getEntity().y = bounds.minY
            } else if (getEntity().bottomY > bounds.maxY) {
                getEntity().y = bounds.maxY - getEntity().height
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

    override fun isComponentInjectionRequired(): Boolean = false
}

/**
 * A component that keeps an entity within the viewport.
 * Entities with physics enabled are not supported.
 * Do NOT use this component if viewport is bound to an entity.
 */
class KeepOnScreenComponent : KeepInBoundsComponent(Rectangle2D.EMPTY) {

    override fun onUpdate(tpf: Double) {
        bounds = FXGL.getGameScene().viewport.visibleArea
        super.onUpdate(tpf)
    }
}