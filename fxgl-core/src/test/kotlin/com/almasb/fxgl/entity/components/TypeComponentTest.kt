/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components

import com.almasb.fxgl.core.serialization.Bundle
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
    fun `Save`() {
        val t1 = TypeComponent(MyType.ONE)
        val bundle = Bundle("")

        t1.write(bundle)

        val t2 = TypeComponent(MyType.TWO)
        t2.read(bundle)

        assertTrue(t2.isType(MyType.ONE))
    }
}