/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
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

    // permutation table
    private val p = IntArray(NOISE_TABLE_SIZE)
    
    private val gx = FloatArray(NOISE_TABLE_SIZE)
    private val gy = FloatArray(NOISE_TABLE_SIZE)

    init {
        setSeedAndReinitialize()
    }

    private fun setSeedAndReinitialize() {
        var i: Int
        var j: Int
        var nSwap: Int

        // Initialize the permutation table
        i = 0
        while (i < NOISE_TABLE_SIZE) {
            p[i] = i
            i++
        }

        i = 0
        while (i < NOISE_TABLE_SIZE) {
            j = FXGLMath.random(1, Int.MAX_VALUE) and NOISE_MASK

            nSwap = p[i]
            p[i] = p[j]
            p[j] = nSwap
            i++
        }

        // Generate the gradient lookup tables
        for (i in 0..NOISE_TABLE_SIZE - 1) {
            // Ken Perlin proposes that the gradients are taken from the unit
            // circle/sphere for 2D/3D.
            // So lets generate a good pseudo-random vector and normalize it

            val v = Vec2()
            // random is in the 0..1 range, so we bring to -0.5..0.5
            v.x = FXGLMath.randomFloat() - 0.5f
            v.y = FXGLMath.randomFloat() - 0.5f
            v.normalizeLocal()

            gx[i] = v.x
            gy[i] = v.y
        }
    }

    //! 1D quality noise generator, good for many situations like up/down movements, flickering/ambient lights etc.
    //! A typical usage would be to pass system time multiplied by a frequency value, like:
    //! float fRes=pNoise->Noise1D(fCurrentTime*fFreq);
    //! the lower the frequency, the smoother the output
    /**
     * Generates a value in [-0.5..0.5), t > 0.
     */
    fun noise1D(t: Double): Double {
        // Compute what gradients to use
        var qx0 = Math.floor(t).toInt()
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

    //! 2D quality noise generator, twice slower than Noise1D.
    //! A typical usage would be to pass 2d coordinates multiplied by a frequency value, like:
    //! float fRes=pNoise->noise2D(fX*fFreq,fY*fFreq);

    // https://github.com/AlmasB/FXGL/issues/479
    fun noise2D(x: Double, y: Double): Double {

        // Compute what gradients to use
        var qx0 = Math.floor(x).toInt()
        var qx1 = qx0 + 1
        val tx0 = x - qx0.toFloat()
        val tx1 = tx0 - 1

        var qy0 = Math.floor(y).toInt()
        var qy1 = qy0 + 1
        val ty0 = y - qy0.toFloat()
        val ty1 = ty0 - 1

        // Make sure we don't come outside the lookup table
        qx0 = qx0 and NOISE_MASK
        qx1 = qx1 and NOISE_MASK

        qy0 = qy0 and NOISE_MASK
        qy1 = qy1 and NOISE_MASK

        // Permutate values to get pseudo randomly chosen gradients
        val q00 = p[qy0 + p[qx0] and NOISE_MASK]
        val q01 = p[qy0 + p[qx1] and NOISE_MASK]

        val q10 = p[qy1 + p[qx0] and NOISE_MASK]
        val q11 = p[qy1 + p[qx1] and NOISE_MASK]

        // Compute the dotproduct between the vectors and the gradients
        val v00 = gx[q00] * tx0 + gy[q00] * ty0
        val v01 = gx[q01] * tx1 + gy[q01] * ty0

        val v10 = gx[q10] * tx0 + gy[q10] * ty1
        val v11 = gx[q11] * tx1 + gy[q11] * ty1

        // Modulate with the weight function
        val wx = (3 - 2 * tx0) * tx0 * tx0
        val v0 = v00 - wx * (v00 - v01)
        val v1 = v10 - wx * (v10 - v11)

        val wy = (3 - 2 * ty0) * ty0 * ty0

        return v0 - wy * (v0 - v1)
    }
}