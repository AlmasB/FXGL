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
class InputMappingTest {

    @Test
    fun `Button input mapping`() {
        val mapping = InputMapping("name", MouseButton.PRIMARY)

        assertTrue(mapping.isButtonTrigger())
        assertFalse(mapping.isKeyTrigger())
        assertThat(mapping.actionName, `is`("name"))
        assertThat(mapping.getButtonTrigger(), `is`(MouseButton.PRIMARY))
        assertThat(mapping.modifier, `is`(InputModifier.NONE))
    }

    @Test
    fun `Key input mapping`() {
        val mapping = InputMapping("name", KeyCode.C)

        assertTrue(mapping.isKeyTrigger())
        assertFalse(mapping.isButtonTrigger())
        assertThat(mapping.actionName, `is`("name"))
        assertThat(mapping.getKeyTrigger(), `is`(KeyCode.C))
        assertThat(mapping.modifier, `is`(InputModifier.NONE))
    }
}