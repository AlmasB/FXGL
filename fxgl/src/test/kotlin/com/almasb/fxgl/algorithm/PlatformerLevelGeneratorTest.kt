/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.algorithm

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.lessThanOrEqualTo
import org.junit.jupiter.api.Test

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class PlatformerLevelGeneratorTest {

    @Test
    fun make() {
        val platforms = PlatformerLevelGenerator.make(PlatformerLevelGenerator.Params(100, 1999L, 10, 15, 1, 3))

        assertThat(platforms.map { it.maxX }.max()!!, lessThanOrEqualTo(100.0))
        assertThat(platforms.map { it.width }.max()!!, lessThanOrEqualTo(15.0))
        assertThat(platforms.map { it.width }.min()!!, greaterThanOrEqualTo(10.0))

        // create pairs (1,2) (2,3) ... (n-1, n) and compute min/max distance
        val distances = platforms.dropLast(1).zip(platforms.drop(1)).map { it.second.minX - it.first.maxX }

        assertThat(distances.min()!!, greaterThanOrEqualTo(1.0))
        assertThat(distances.max()!!, lessThanOrEqualTo(3.0))
    }
}