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
class CircleShapeTest {

    private lateinit var circle: CircleShape

    // example circles
    private lateinit var c0: CircleShape
    private lateinit var c1: CircleShape
    private lateinit var c2: CircleShape
    private lateinit var c3: CircleShape

    @BeforeEach
    fun `setUp`() {
        c0 = CircleShape(0.5f)
        c0.center.setZero()

        c1 = CircleShape(2.5f)
        c1.center.set(-2.5f, 3.2f)

        c2 = CircleShape(8.25f)
        c2.center.set(-4.5f, -2.5f)

        c3 = CircleShape(12.0f)
        c3.center.set(4.5f, 2.5f)
    }

    @Test
    fun `Compute AABB`() {
        var aabb = AABB()

        c0.computeAABB(aabb, Transform(), 0)

        assertThat(aabb.lowerBound, `is`(Vec2(-0.5,-0.5)))
        assertThat(aabb.upperBound, `is`(Vec2(0.5,0.5)))

        // c1

        aabb = AABB()

        c1.computeAABB(aabb, Transform(), 0)

        assertThat(aabb.lowerBound, `is`(Vec2(-5.0,0.70000005)))
        assertThat(aabb.upperBound, `is`(Vec2(0.0,5.7)))

        // c2

        aabb = AABB()

        c2.computeAABB(aabb, Transform(), 0)

        assertThat(aabb.lowerBound, `is`(Vec2(-12.75,-10.75)))
        assertThat(aabb.upperBound, `is`(Vec2(3.75,5.75)))

        // c3

        aabb = AABB()

        c3.computeAABB(aabb, Transform(), 0)

        assertThat(aabb.lowerBound, `is`(Vec2(-7.5,-9.5)))
        assertThat(aabb.upperBound, `is`(Vec2(16.5,14.5)))

        // different transform

        aabb = AABB()

        val t = Transform()
        t.set(Vec2(3f, -1.2f), 33f)

        c3.computeAABB(aabb, t, 0)

        assertThat(aabb.lowerBound, `is`(Vec2(-11.559607,-8.733637)))
        assertThat(aabb.upperBound, `is`(Vec2(12.440393,15.266363)))
    }
}