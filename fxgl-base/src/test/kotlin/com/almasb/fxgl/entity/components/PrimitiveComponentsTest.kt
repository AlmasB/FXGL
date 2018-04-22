/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components

import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.components.PositionComponent
import com.almasb.fxgl.io.serialization.Bundle
import javafx.geometry.Point2D
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.function.Executable

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class PrimitiveComponentsTest {

    @Test
    fun `Boolean`() {
        val c = SimpleBooleanComponent()

        assertFalse(c.value)
        assertFalse(c.valueProperty().value)

        c.value = true
        assertTrue(c.value)

        val bundle = Bundle("")
        c.write(bundle)

        c.value = false
        c.read(bundle)

        assertTrue(c.value)

        assertThat(c.toString(), `is`("SimpleBooleanComponent[value=true]"))
    }

    @Test
    fun `Collidable`() {
        val c = CollidableComponent(false)

        assertFalse(c.value)

        c.value = true

        val c2 = c.copy()

        assertFalse(c === c2)
        assertTrue(c2.value)
        assertThat(c.toString(), `is`("CollidableComponent[value=true]"))
    }

    class SimpleBooleanComponent : BooleanComponent()
}