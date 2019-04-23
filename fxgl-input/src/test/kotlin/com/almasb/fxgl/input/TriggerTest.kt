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
import org.junit.jupiter.api.Assertions.*
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

        assertTrue(key.isKey)
        assertFalse(key.isButton)
        assertThat(key.modifier, `is`(InputModifier.NONE))
        assertThat(key.key, `is`(KeyCode.A))
        assertThat(key.name, `is`("A"))
    }

    @Test
    fun `Mouse trigger`() {
        val btn = MouseTrigger(MouseButton.PRIMARY)

        assertFalse(btn.isKey)
        assertTrue(btn.isButton)
        assertThat(btn.modifier, `is`(InputModifier.NONE))
        assertThat(btn.button, `is`(MouseButton.PRIMARY))
        assertThat(btn.name, `is`("LMB"))
    }

    @Test
    fun `Test toString`() {
        val key = KeyTrigger(KeyCode.A, InputModifier.CTRL)

        assertThat(key.toString(), `is`("CTRL+A"))

        val btn = MouseTrigger(MouseButton.PRIMARY, InputModifier.ALT)

        assertThat(btn.toString(), `is`("ALT+LMB"))
    }

    @Test
    fun `toString throws if not a button`() {
        assertThrows(RuntimeException::class.java) {
            MouseTrigger(MouseButton.NONE).toString()
        }
    }

    @Test
    fun `Mouse button from string`() {
        assertThat(MouseTrigger.buttonFromString("LMB"), `is`(MouseButton.PRIMARY))
        assertThat(MouseTrigger.buttonFromString("MMB"), `is`(MouseButton.MIDDLE))
        assertThat(MouseTrigger.buttonFromString("RMB"), `is`(MouseButton.SECONDARY))
    }

    @Test
    fun `Mouse button from string throws if not LMB MMB or RMB`() {
        assertThrows(RuntimeException::class.java) {
            MouseTrigger.buttonFromString("")
        }
    }
}