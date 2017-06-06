/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene.lighting

import javafx.geometry.Point2D

/**
 * Adapted from https://github.com/timyates/ShadowFX
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Segment(val from: Point2D, to: Point2D) {

    val to: Point2D

    init {
        this.to = Point2D(to.x - from.x, to.y - from.y)
    }

    fun magnitude() = Math.sqrt(to.x * to.x + to.y * to.y)
}