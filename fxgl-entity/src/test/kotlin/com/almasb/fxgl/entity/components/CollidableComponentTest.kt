/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.entity.components

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.contains
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.Serializable

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class CollidableComponentTest {

    private enum class MyType {
        ONE, TWO
    }

    @Test
    fun `Ignored types`() {
        val c = CollidableComponent(true)
        c.addIgnoredType(MyType.ONE)
        assertThat(c.getIgnoredTypes(), contains<Serializable>(MyType.ONE))

        c.removeIgnoredType(MyType.ONE)
        assertTrue(c.getIgnoredTypes().isEmpty())
    }

    @Test
    fun `Copy`() {
        val c = CollidableComponent(true)
        c.addIgnoredType(MyType.ONE)
        c.addIgnoredType(MyType.TWO)

        val c2 = c.copy()

        assertThat(c2.getIgnoredTypes(), contains<Serializable>(MyType.ONE, MyType.TWO))
        assertThat(c2.value, `is`(c.value))
    }
}