/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.dsl.components

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ActivatorComponentTest {

    private lateinit var activator: ActivatorComponent

    @Test
    fun `ActivatorComponent can be activated and deactivated`() {
        activator = ActivatorComponent(true, 3)

        assertTrue(activator.canBeDeactivated)
        assertThat(activator.numTimesCanBeActivated, `is`(3))
        assertFalse(activator.isActivated)
        assertFalse(activator.valueProperty().value)

        // activated 1
        activator.activate()
        assertTrue(activator.isActivated)
        assertTrue(activator.valueProperty().value)

        activator.deactivate()
        assertFalse(activator.isActivated)
        assertFalse(activator.valueProperty().value)

        // activated 2
        activator.press()
        assertTrue(activator.isActivated)
        assertTrue(activator.valueProperty().value)

        activator.press()
        assertFalse(activator.isActivated)
        assertFalse(activator.valueProperty().value)

        // activated 3
        activator.activate()
        assertTrue(activator.isActivated)
        assertTrue(activator.valueProperty().value)

        activator.deactivate()
        assertFalse(activator.isActivated)
        assertFalse(activator.valueProperty().value)

        // should not activate again
        activator.activate()
        assertFalse(activator.isActivated)
        assertFalse(activator.valueProperty().value)
    }
}