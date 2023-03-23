/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.collision

import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.physics.box2d.collision.shapes.CircleShape
import com.almasb.fxgl.physics.box2d.collision.shapes.PolygonShape
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DistanceProxyTest {

    private lateinit var proxy: DistanceProxy

    @BeforeEach
    fun `setUp`() {
        proxy = DistanceProxy()
    }

    @Test
    fun `distance proxy polygon shape`() {
        val shape = PolygonShape()
        shape.setAsBox(0.5f, 0.5f)

        proxy.set(shape, 0)

        assertThat(proxy.getVertex(0), `is`(Vec2(-0.5f, -0.5f)))
        assertThat(proxy.radius, `is`(0.01f))

        val support = proxy.getSupport(Vec2(1.0, 0.0))

        assertThat(support, `is`(1))
    }

    @Test
    fun `distance proxy circle shape`() {
        val shape = CircleShape(2.5f)
        shape.center.set(-2.5f, 3.2f)

        proxy.set(shape, 0)

        assertThat(proxy.getVertex(0), `is`(Vec2(-2.5f, 3.2f)))
        assertThat(proxy.radius, `is`(2.5f))

        val support = proxy.getSupport(Vec2(1.0, 0.0))

        assertThat(support, `is`(0))
    }
}