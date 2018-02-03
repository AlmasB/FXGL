/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.algorithm.procedural

import com.almasb.fxgl.core.collection.Grid
import com.almasb.fxgl.core.math.FXGLMath

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class HeightMapGenerator
@JvmOverloads constructor(var frequency: Double = 10.0) : MapGenerator<Double> {

    /**
     * First two params are normalized [-0.5..0.5] x and y,
     * Third param is frequency.
     */
    var genFunction: (Double, Double, Double) -> Double = { nx, ny, frequency ->
        FXGLMath.noise2D(frequency * nx, frequency * ny)
    }

    override fun generate(width: Int, height: Int): Grid<Double> {

        return Grid(width, height, { x, y ->
            val nx = x * 1.0 / width - 0.5
            val ny = y * 1.0 / height - 0.5

            genFunction.invoke(nx, ny, frequency)
        })
    }
}