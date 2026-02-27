/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.physics.box2d.collision.shapes

import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.physics.box2d.collision.AABB
import com.almasb.fxgl.physics.box2d.common.Transform
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ChainShapeTest {

    // example shapes
    private lateinit var e0: ChainShape
    private lateinit var e1: ChainShape
    private lateinit var e2: ChainShape
    private lateinit var e3: ChainShape

    @BeforeEach
    fun `setUp`() {
        e0 = ChainShape(ChainType.CLOSED,
                arrayOf(
                        Vec2(-1f, 0f),
                        Vec2(-1f, 1f),
                        Vec2(2f, 0.5f),
                        Vec2(3f, 0.25f),
                        Vec2(1f, 0f)
                )
        )

        e1 = ChainShape(ChainType.CLOSED,
                arrayOf(
                        Vec2(-2f, 2f),
                        Vec2(-3f, 2f),
                        Vec2(0f, 3.5f),
                        Vec2(3f, 0.25f)
                )
        )

        e2 = ChainShape(ChainType.CLOSED,
                arrayOf(
                        Vec2(-4f, -2f),
                        Vec2(-5f, -2f),
                        Vec2(-5f, 3.5f),
                        Vec2(-3f, 2.25f),
                        Vec2(-1.5f, 5.15f),
                        Vec2(8f, -4f),
                        Vec2(0f, 0f)
                )
        )
    }

    @Test
    fun `Compute AABB`() {
        var aabb = AABB()

        e0.computeAABB(aabb, Transform(), 0)

        assertThat(aabb.lowerBound, `is`(Vec2(-1.0,0.0)))
        assertThat(aabb.upperBound, `is`(Vec2(-1.0,1.0)))

        // 1

        aabb = AABB()

        e1.computeAABB(aabb, Transform(), 0)

        assertThat(aabb.lowerBound, `is`(Vec2(-3.0,2.0)))
        assertThat(aabb.upperBound, `is`(Vec2(-2.0,2.0)))

        // 2

        aabb = AABB()

        e2.computeAABB(aabb, Transform(), 0)

        assertThat(aabb.lowerBound, `is`(Vec2(-5.0,-2.0)))
        assertThat(aabb.upperBound, `is`(Vec2(-4.0,-2.0)))

        // 3

        aabb = AABB()

        val t = Transform()
        t.p.set(2.5f, -1f)
        t.q.set(35f)

        e2.computeAABB(aabb, t, 0)

        assertThat(aabb.lowerBound, `is`(Vec2(5.2584343,2.5198894)))
        assertThat(aabb.upperBound, `is`(Vec2(6.1621103,2.948024)))
    }

    @Test
    fun `Create a loop`() {
        val c = e1

        assertThat(c.count, `is`(5))
        assertThat(c.getVertex(0), `is`(Vec2(-2f, 2f)))
        assertThat(c.getVertex(1), `is`(Vec2(-3f, 2f)))
        assertThat(c.getVertex(2), `is`(Vec2(0f, 3.5f)))
        assertThat(c.getVertex(3), `is`(Vec2(3f, 0.25f)))
        assertThat(c.getVertex(4), `is`(Vec2(-2f, 2f)))
    }

    @Test
    fun `Create a chain`() {
        val c = ChainShape(ChainType.OPEN, arrayOf(
                Vec2(-2f, 2f),
                Vec2(-3f, 2f),
                Vec2(0f, 3.5f),
                Vec2(3f, 0.25f)
        ))

        assertThat(c.count, `is`(4))
        assertThat(c.getVertex(0), `is`(Vec2(-2f, 2f)))
        assertThat(c.getVertex(1), `is`(Vec2(-3f, 2f)))
        assertThat(c.getVertex(2), `is`(Vec2(0f, 3.5f)))
        assertThat(c.getVertex(3), `is`(Vec2(3f, 0.25f)))
    }
}