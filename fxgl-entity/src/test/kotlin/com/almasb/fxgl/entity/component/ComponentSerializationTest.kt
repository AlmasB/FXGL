/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.component

import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.entity.components.BooleanComponent
import com.almasb.fxgl.entity.components.DoubleComponent
import com.almasb.fxgl.entity.components.IntegerComponent
import com.almasb.fxgl.entity.components.StringComponent
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ComponentSerializationTest {

    @Test
    fun `Boolean subtype is serializable`() {
        val comp1 = BooleanSubType()
        comp1.value = true

        val bundle = Bundle("sometype")
        comp1.write(bundle)

        val comp2 = BooleanSubType()
        comp2.read(bundle)

        assertTrue(comp2.value)
    }

    @Test
    fun `Int subtype is serializable`() {
        val comp1 = IntSubType()
        comp1.value = 33

        val bundle = Bundle("sometype")
        comp1.write(bundle)

        val comp2 = IntSubType()
        comp2.read(bundle)

        assertThat(comp2.value, `is`(33))
    }

    @Test
    fun `Double subtype is serializable`() {
        val comp1 = DoubleSubType()
        comp1.value = 33.0

        val bundle = Bundle("sometype")
        comp1.write(bundle)

        val comp2 = DoubleSubType()
        comp2.read(bundle)

        assertThat(comp2.value, `is`(33.0))
    }

    @Test
    fun `String subtype is serializable`() {
        val comp1 = StringSubType()
        comp1.value = "Hello"

        val bundle = Bundle("sometype")
        comp1.write(bundle)

        val comp2 = StringSubType()
        comp2.read(bundle)

        assertThat(comp2.value, `is`("Hello"))
    }

    private class BooleanSubType : BooleanComponent()
    private class IntSubType : IntegerComponent()
    private class DoubleSubType : DoubleComponent()
    private class StringSubType : StringComponent()
}