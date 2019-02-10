/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components

import org.hamcrest.MatcherAssert.assertThat
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
}