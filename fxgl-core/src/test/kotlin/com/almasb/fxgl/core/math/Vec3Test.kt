/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.core.math

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Vec3Test {

    @Test
    fun `Copy and clone`() {
        val v1 = Vec3()
        val v2 = v1.copy()

        assertTrue(v1 == v2)
        assertFalse(v1 === v2)
    }

    @Test
    fun `Copy ctor`() {
        val v1 = Vec3(Vec3(3f, 5f, 3f))

        assertTrue(v1.x == 3f)
        assertTrue(v1.y == 5f)
        assertThat(v1.z, `is`(3f))
    }

    @Test
    fun `Set zero`() {
        val v1 = Vec3(13.0f, 10.0f, 3f)
        v1.setZero()

        assertTrue(v1.x == 0.0f)
        assertTrue(v1.y == 0.0f)
        assertThat(v1.z, `is`(0f))
    }

    @Test
    fun `Operations`() {
        val v = Vec3(5.0, 5.0, 5.0)
        v.addLocal(Vec3(3.0, -5.0, 15.0))

        assertThat(v, `is`(Vec3(8.0, 0.0, 20.0)))

        v.subLocal(Vec3(2.0, 3.0, 4.0))

        assertThat(v, `is`(Vec3(6.0, -3.0, 16.0)))

        v.mulLocal(3f)

        assertThat(v, `is`(Vec3(18.0, -9.0, 48.0)))

        v.negateLocal()

        assertThat(v, `is`(Vec3(-18.0, 9.0, -48.0)))
    }

    @Test
    fun `To String`() {
        val v1 = Vec3(5.0f, 2.0f, 4f)

        assertThat(v1.toString(), `is`("(5.0,2.0,4.0)"))
    }

    @Test
    fun `Dot product`() {
        val result = Vec3.dot(Vec3(5.0, 5.0, 5.0), Vec3(5.0f, 2.0f, 4f))

        assertThat(result, `is`(55f))
    }

    @Test
    fun `Cross product`() {
        val result = Vec3.cross(Vec3(5.0, 5.0, 5.0), Vec3(5.0f, 2.0f, 4f))

        assertThat(result, `is`(Vec3(10.0, 5.0, -15.0)))
    }
}