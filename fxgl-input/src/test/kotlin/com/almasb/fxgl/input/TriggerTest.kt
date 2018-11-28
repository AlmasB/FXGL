/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input

import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
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
class TriggerTest {

    @Test
    fun `Key trigger`() {
        val key = KeyTrigger(KeyCode.A)

        assertTrue(key.isKey())
        assertFalse(key.isButton())
        assertThat(key.getModifier(), `is`(InputModifier.NONE))
        assertThat(key.key, `is`(KeyCode.A))
        assertThat(key.getName(), `is`("A"))
    }

    @Test
    fun `Mouse trigger`() {
        val btn = MouseTrigger(MouseButton.PRIMARY)

        assertFalse(btn.isKey())
        assertTrue(btn.isButton())
        assertThat(btn.getModifier(), `is`(InputModifier.NONE))
        assertThat(btn.button, `is`(MouseButton.PRIMARY))
        assertThat(btn.getName(), `is`("LMB"))
    }

    @Test
    fun `Test toString`() {
        val key = KeyTrigger(KeyCode.A, InputModifier.CTRL)

        assertThat(key.toString(), `is`("CTRL+A"))

        val btn = MouseTrigger(MouseButton.PRIMARY, InputModifier.ALT)

        assertThat(btn.toString(), `is`("ALT+LMB"))
    }
}