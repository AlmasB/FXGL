/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.math

import com.almasb.fxgl.entity.Entity
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FXGLMathTest {

    @Test
    fun `Random array element returns null if array is empty`() {
        val array = Array<Entity>(0, { Entity() })

        val element = FXGLMath.random(array)
        assertFalse(element.isPresent)
    }

    @Test
    fun `Random array element returns it, if single item`() {
        val e = Entity()
        val array = Array<Entity>(1, { e })

        val element = FXGLMath.random(array)
        assertThat(element.get(), `is`(e))
    }

    @Test
    fun `Random list element returns null if list is empty`() {
        val list = listOf<Entity>()

        val element = FXGLMath.random(list)
        assertFalse(element.isPresent)
    }

    @Test
    fun `Random list element returns it, if single item`() {
        val e = Entity()
        val list = listOf<Entity>(e)

        val element = FXGLMath.random(list)
        assertThat(element.get(), `is`(e))
    }
}