/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input

import javafx.scene.input.KeyCode
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class InputModifierTest {

    private lateinit var input: Input

    @BeforeEach
    fun setUp() {
        input = Input()
    }

    @Test
    fun `Convert Shift from MouseEvent`() {
        val e = mouseEvent(true, false, false)

        val modifier = InputModifier.from(e)
        assertThat(modifier, `is`(InputModifier.SHIFT))
    }

    @Test
    fun `Convert Ctrl from MouseEvent`() {
        val e = mouseEvent(false, true, false)

        val modifier = InputModifier.from(e)
        assertThat(modifier, `is`(InputModifier.CTRL))
    }

    @Test
    fun `Convert Alt from MouseEvent`() {
        val e = mouseEvent(false, false, true)

        val modifier = InputModifier.from(e)
        assertThat(modifier, `is`(InputModifier.ALT))
    }

    @Test
    fun `Convert from MouseEvent`() {
        val e = mouseEvent(false, false, false)

        val modifier = InputModifier.from(e)
        assertThat(modifier, `is`(InputModifier.NONE))
    }

    @Test
    fun `Convert Shift from KeyEvent`() {
        val e = keyEvent(true, false, false)

        val modifier = InputModifier.from(e)
        assertThat(modifier, `is`(InputModifier.SHIFT))
    }

    @Test
    fun `Convert Ctrl from KeyEvent`() {
        val e = keyEvent(false, true, false)

        val modifier = InputModifier.from(e)
        assertThat(modifier, `is`(InputModifier.CTRL))
    }

    @Test
    fun `Convert Alt from KeyEvent`() {
        val e = keyEvent(false, false, true)

        val modifier = InputModifier.from(e)
        assertThat(modifier, `is`(InputModifier.ALT))
    }

    @Test
    fun `Convert from KeyEvent`() {
        val e = keyEvent(false, false, false)

        val modifier = InputModifier.from(e)
        assertThat(modifier, `is`(InputModifier.NONE))
    }

    @Test
    fun `Is triggered mouse`() {
        assertTrue(InputModifier.SHIFT.isTriggered(mouseEvent(true, false, false)))
        assertTrue(InputModifier.CTRL.isTriggered(mouseEvent(false, true, false)))
        assertTrue(InputModifier.ALT.isTriggered(mouseEvent(false, false, true)))
        assertTrue(InputModifier.NONE.isTriggered(mouseEvent(false, false, false)))

        assertFalse(InputModifier.NONE.isTriggered(mouseEvent(true, false, false)))
        assertFalse(InputModifier.NONE.isTriggered(mouseEvent(false, true, false)))
        assertFalse(InputModifier.NONE.isTriggered(mouseEvent(false, false, true)))
    }

    @Test
    fun `Is triggered key`() {
        assertTrue(InputModifier.SHIFT.isTriggered(keyEvent(true, false, false)))
        assertTrue(InputModifier.CTRL.isTriggered(keyEvent(false, true, false)))
        assertTrue(InputModifier.ALT.isTriggered(keyEvent(false, false, true)))
        assertTrue(InputModifier.NONE.isTriggered(keyEvent(false, false, false)))

        assertFalse(InputModifier.NONE.isTriggered(keyEvent(true, false, false)))
        assertFalse(InputModifier.NONE.isTriggered(keyEvent(false, true, false)))
        assertFalse(InputModifier.NONE.isTriggered(keyEvent(false, false, true)))
    }

    @Test
    fun `To KeyCode`() {
        assertTrue(InputModifier.ALT.toKeyCode() == KeyCode.ALT)
        assertTrue(InputModifier.CTRL.toKeyCode() == KeyCode.CONTROL)
        assertTrue(InputModifier.SHIFT.toKeyCode() == KeyCode.SHIFT)
        assertTrue(InputModifier.NONE.toKeyCode() == KeyCode.ALPHANUMERIC)
    }

    @Test
    fun `Do not release illegal keys when main key is released`() {
        var callsShift = 0

        input.addTriggerListener(object : TriggerListener() {
            override fun onKey(keyTrigger: KeyTrigger) {
                if (keyTrigger.key == KeyCode.SHIFT) {
                    callsShift++
                }
            }

            override fun onKeyEnd(keyTrigger: KeyTrigger) {
                if (keyTrigger.key == KeyCode.SHIFT) {
                    callsShift = 999
                }
            }
        })

        input.update(0.0)
        assertThat(callsShift, `is`(0))

        // press shift
        input.mockKeyPress(KeyCode.SHIFT)
        input.update(0.0)
        assertThat(callsShift, `is`(1))

        // press arbitrary key
        input.mockKeyPress(KeyCode.D)
        input.update(0.0)
        assertThat(callsShift, `is`(2))

        // release that arbitrary key
        input.mockKeyRelease(KeyCode.D)
        input.update(0.0)
        assertThat(callsShift, `is`(3))

        // shift must still remain to be active
        input.update(0.0)
        assertThat(callsShift, `is`(4))
    }
}