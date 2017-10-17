/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.control

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.entity.Control
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.component.BoundingBoxComponent
import com.almasb.fxgl.entity.component.PositionComponent
import javafx.geometry.Rectangle2D
import javafx.util.Duration

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class RandomMoveControl
@JvmOverloads constructor(
        var speed: Double,
        var xSeed: Double = FXGLMath.random(100f, 10000f).toDouble(),
        var ySeed: Double = FXGLMath.random(10000f, 100000f).toDouble(),
        var bounds: Rectangle2D = FXGL.getApp().appBounds) : Control() {

    private lateinit var position: PositionComponent
    private var bbox: BoundingBoxComponent? = null

    private val nextPosition = Vec2()

    private val timer = FXGL.newLocalTimer()
    private val delay = Duration.seconds(1500 / speed)

    override fun onAdded(entity: Entity) {
        nextPosition.set(position.value)
        timer.capture()
    }

    override fun onUpdate(entity: Entity, tpf: Double) {
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
        val maxX = bounds.maxX - (bbox?.width ?: 0.0)
        val maxY = bounds.maxY - (bbox?.height ?: 0.0)

        val x = FXGLMath.map(FXGLMath.noise1D(xSeed) * 1.0, 0.0, 1.0, bounds.minX, maxX).toFloat()
        val y = FXGLMath.map(FXGLMath.noise1D(ySeed) * 1.0, 0.0, 1.0, bounds.minY, maxY).toFloat()

        nextPosition.set(x, y)
    }
}