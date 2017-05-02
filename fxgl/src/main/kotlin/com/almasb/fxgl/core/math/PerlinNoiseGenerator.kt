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

package com.almasb.fxgl.core.math

/**
 * Adapted from https://github.com/CRYTEK/CRYENGINE/blob/release/Code/CryEngine/CryCommon/CryMath/PNoise3.h
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal object PerlinNoiseGenerator {

    // from CRYtek
    private val NOISE_TABLE_SIZE = 256
    private val NOISE_MASK = 255

    private val gx = FloatArray(NOISE_TABLE_SIZE)
    private val gy = FloatArray(NOISE_TABLE_SIZE)

    init {
        setSeedAndReinitialize()
    }

    private fun setSeedAndReinitialize() {
        // Generate the gradient lookup tables
        for (i in 0..NOISE_TABLE_SIZE - 1) {
            // Ken Perlin proposes that the gradients are taken from the unit
            // circle/sphere for 2D/3D.
            // So lets generate a good pseudo-random vector and normalize it

            val v = Vec2()
            // random is in the 0..1 range, so we bring to -0.5..0.5
            v.x = FXGLMath.random() - 0.5f
            v.y = FXGLMath.random() - 0.5f
            v.normalizeLocal()

            gx[i] = v.x
            gy[i] = v.y
        }
    }

    /**
     * Generates a value in [-0.5..0.5), t > 0.
     */
    fun noise1D(t: Float): Float {
        // Compute what gradients to use
        var qx0 = Math.floor(t.toDouble()).toInt()
        var qx1 = qx0 + 1
        val tx0 = t - qx0
        val tx1 = tx0 - 1

        // Make sure we don't come outside the lookup table
        qx0 = qx0 and NOISE_MASK
        qx1 = qx1 and NOISE_MASK

        // Compute the dotproduct between the vectors and the gradients
        val v0 = gx[qx0] * tx0
        val v1 = gx[qx1] * tx1

        // Modulate with the weight function
        val wx = (3 - 2 * tx0) * tx0 * tx0
        return v0 - wx * (v0 - v1)
    }
}