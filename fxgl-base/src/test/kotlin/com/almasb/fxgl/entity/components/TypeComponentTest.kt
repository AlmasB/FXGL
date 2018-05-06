/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TypeComponentTest {

    private enum class MyType {
        ONE, TWO
    }

    @Test
    fun `Copy`() {
        val t1 = TypeComponent(MyType.ONE)
        val t2 = t1.copy()

        assertTrue(t2.isType(MyType.ONE))
    }
}