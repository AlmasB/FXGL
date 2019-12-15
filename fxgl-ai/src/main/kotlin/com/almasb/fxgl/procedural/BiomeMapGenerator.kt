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
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
open class BiomeMapGenerator
@JvmOverloads constructor(
        val width: Int,
        val height: Int,
        var frequency: Double = 10.0) : CellGenerator<BiomeMapGenerator.BiomeData> {

    class BiomeData(x: Int, y: Int, var elevation: Double, var moisture: Double) : Cell(x, y)

    override fun apply(x: Int, y: Int): BiomeData {
        val nx = x * 1.0 / width - 0.5
        val ny = y * 1.0 / height - 0.5

        // https://github.com/AlmasB/FXGL/issues/473
        return BiomeData(x, y, FXGLMath.noise2D(frequency * nx, frequency * ny), FXGLMath.noise2D(frequency * nx, frequency * ny))
    }
}