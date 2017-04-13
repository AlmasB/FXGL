/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.entity.control

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.ecs.AbstractControl
import com.almasb.fxgl.ecs.Entity
import com.almasb.fxgl.entity.component.BoundingBoxComponent
import com.almasb.fxgl.entity.component.PositionComponent
import javafx.util.Duration

/**
 * TODO: allow selecting random number generation technique and initial seeds?
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class RandomMoveControl(var speed: Double) : AbstractControl() {

    private lateinit var position: PositionComponent
    private var bbox: BoundingBoxComponent? = null

    private val nextPosition = Vec2()
    private var xSeed = FXGLMath.random(100f, 10000f).toDouble()
    private var ySeed = FXGLMath.random(10000f, 100000f).toDouble()

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
        val rangeX = FXGL.getAppWidth() - (bbox?.width?.toFloat() ?: 0.0f)
        val rangeY = FXGL.getAppHeight() - (bbox?.height?.toFloat() ?: 0.0f)

        nextPosition.set(FXGLMath.noise1D(xSeed) * rangeX, FXGLMath.noise1D(ySeed) * rangeY)
    }
}