/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.extra.entity.components

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.components.BoundingBoxComponent
import com.almasb.fxgl.entity.components.PositionComponent
import javafx.geometry.Rectangle2D
import javafx.util.Duration

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class RandomMoveComponent
@JvmOverloads constructor(
        var speed: Double,
        var xSeed: Double = FXGLMath.random(100, 10000).toDouble(),
        var ySeed: Double = FXGLMath.random(10000, 100000).toDouble(),
        var bounds: Rectangle2D = FXGL.getApp().appBounds) : Component() {

    private lateinit var position: PositionComponent
    private var bbox: BoundingBoxComponent? = null

    private val nextPosition = Vec2()

    private val timer = FXGL.newLocalTimer()
    private val delay = Duration.seconds(1500 / speed)

    override fun onAdded() {
        nextPosition.set(position.value)
        timer.capture()
    }

    override fun onUpdate(tpf: Double) {
        xSeed += tpf
        ySeed += tpf

        if (nextPosition.distanceLessThanOrEqual(position.x, position.y, speed * tpf)) {
            updateNextPosition()
        } else {
            position.translateTowards(nextPosition.toPoint2D(), speed * tpf)
        }

        if (timer.elapsed(delay)) {
            updateNextPosition()
            timer.capture()
        }
    }

    private fun updateNextPosition() {
        val maxX = bounds.maxX - (bbox?.getWidth() ?: 0.0)
        val maxY = bounds.maxY - (bbox?.getHeight() ?: 0.0)

        val x = FXGLMath.map(FXGLMath.noise1D(xSeed) * 1.0, 0.0, 1.0, bounds.minX, maxX).toFloat()
        val y = FXGLMath.map(FXGLMath.noise1D(ySeed) * 1.0, 0.0, 1.0, bounds.minY, maxY).toFloat()

        nextPosition.set(x, y)
    }
}