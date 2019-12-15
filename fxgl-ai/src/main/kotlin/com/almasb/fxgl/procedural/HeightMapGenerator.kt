/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.procedural

import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.pathfinding.Cell
import com.almasb.fxgl.pathfinding.CellGenerator

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
open class HeightMapGenerator
@JvmOverloads constructor(val width: Int,
                          val height: Int,
                          var frequency: Double = 10.0) : CellGenerator<HeightMapGenerator.HeightData> {

    class HeightData(x: Int, y: Int, var height: Double) : Cell(x, y)

    override fun apply(x: Int, y: Int): HeightData {
        val nx = x * 1.0 / width - 0.5
        val ny = y * 1.0 / height - 0.5

        val height = FXGLMath.noise2D(frequency * nx, frequency * ny)

        return HeightData(x, y, height)
    }
}