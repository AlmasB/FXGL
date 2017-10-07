/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.algorithm

import com.almasb.fxgl.core.collection.ObjectMap
import com.almasb.fxgl.core.math.FXGLMath
import javafx.geometry.Rectangle2D
import java.util.*

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object PlatformerLevelGenerator {

    private val randoms = ObjectMap<Long, Random>()

    fun make(params: Params): List<Rectangle2D> {

        // update cached randoms
        if (!randoms.containsKey(params.randomSeed)) {
            randoms.put(params.randomSeed, FXGLMath.getRandom(params.randomSeed))
        }

        val result = arrayListOf<Rectangle2D>()

        var length = 0

        while (length < params.levelLength) {

            val pLength = pLength(params)

            if (length + pLength > params.levelLength) {
                break
            }

            result.add(Rectangle2D(length.toDouble(), 0.0, pLength.toDouble(), 1.0))

            length += pLength + dist(params)
        }

        return result
    }

    private fun pLength(params: Params): Int {
        val offset = randoms[params.randomSeed].nextInt(params.maxPlatformLength - params.minPlatformLength + 1)

        return params.minPlatformLength + offset
    }

    private fun dist(params: Params): Int {
        val offset = randoms[params.randomSeed].nextInt(params.maxPlatformDistance - params.minPlatformDistance + 1)

        return params.minPlatformDistance + offset
    }

    data class Params(val levelLength: Int,
                      val randomSeed: Long,
                      val minPlatformLength: Int,
                      val maxPlatformLength: Int,
                      val minPlatformDistance: Int,
                      val maxPlatformDistance: Int)
}