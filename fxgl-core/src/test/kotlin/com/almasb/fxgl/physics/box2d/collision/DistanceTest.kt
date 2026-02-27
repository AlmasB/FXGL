/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.collision

import com.almasb.fxgl.physics.box2d.collision.Distance.SimplexCache
import com.almasb.fxgl.physics.box2d.collision.shapes.CircleShape
import com.almasb.fxgl.physics.box2d.collision.shapes.PolygonShape
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DistanceTest {

    @Test
    fun `distance poly poly`() {
        val distance = Distance()

        val shapeA = PolygonShape()
        shapeA.setAsBox(0.5f, 0.5f)

        val shapeB = PolygonShape()
        shapeB.setAsBox(0.5f, 0.5f)

        val output = DistanceOutput()

        val input = DistanceInput()
        input.proxyA.set(shapeA, 0)
        input.proxyB.set(shapeB, 0)
        input.transformB.p.set(3f, 0.5f)

        val cache = SimplexCache()
        cache.count = 0

        distance.distance(output, cache, input)

        assertThat(output.distance, `is`(2f))
    }

    @Test
    fun `distance poly circle`() {
        val distance = Distance()

        val shapeA = PolygonShape()
        shapeA.setAsBox(0.5f, 0.5f)

        val shapeB = CircleShape(2.5f, 3f, 3.2f)

        val output = DistanceOutput()

        val input = DistanceInput()
        input.proxyA.set(shapeA, 0)
        input.proxyB.set(shapeB, 0)
        input.transformB.p.set(3f, 0.5f)

        val cache = SimplexCache()
        cache.count = 0

        distance.distance(output, cache, input)

        assertThat(output.distance, `is`(5.8309517f))
    }
}