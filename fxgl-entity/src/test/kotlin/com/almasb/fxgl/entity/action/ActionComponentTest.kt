/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.action

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ActionComponentTest {

    private lateinit var comp: ActionComponent

    @BeforeEach
    fun setUp() {
        comp = ActionComponent()
    }

    @Test
    fun `Creation`() {
        assertFalse(comp.hasNextActions())

        assertNotNull(comp.currentAction)
        assertNotNull(comp.nextAction)

        assertTrue(comp.isIdle)
    }

    @Test
    fun `Add remove action and callbacks`() {
        val action = TestInstantAction()
        val action2 = TestInstantAction()

        assertThat(action.count, `is`(0))
        assertThat(action2.count, `is`(0))

        comp.addAction(action)
        comp.addAction(action2)

        assertThat(action.count, `is`(1))
        assertThat(action2.count, `is`(1))
        assertFalse(action.isComplete)
        assertFalse(action2.isComplete)
        assertFalse(action.isPerformed)
        assertFalse(action2.isPerformed)

        comp.onUpdate(0.016)

        assertThat(action.count, `is`(2))
        assertThat(action2.count, `is`(1))
        assertTrue(action.isPerformed)
        assertFalse(action2.isPerformed)
        assertThat(comp.actionsProperty().size, `is`(2))

        comp.onUpdate(0.016)

        assertThat(action.count, `is`(3))
        assertThat(action2.count, `is`(2))
        assertTrue(action.isComplete)
        assertTrue(action2.isComplete)
        assertTrue(action.isPerformed)
        assertTrue(action2.isPerformed)
        assertThat(comp.actionsProperty().size, `is`(1))

        comp.onUpdate(0.016)

        assertThat(action2.count, `is`(3))
        assertThat(comp.actionsProperty().size, `is`(0))
    }

    @Test
    fun `Cancel action`() {
        val action = TestInstantAction()

        comp.addAction(action)

        action.cancel()
        assertTrue(action.isCancelled)
        assertThat(action.count, `is`(4))

        comp.onUpdate(0.016)

        assertFalse(action.isComplete)
        assertFalse(action.isPerformed)
    }

    private class TestInstantAction : InstantAction() {
        var count = 0
        var isPerformed = false

        override fun onQueued() {
            count = 1
        }

        override fun onStarted() {
            count = 2
        }

        override fun onCompleted() {
            count = 3
        }

        override fun onCancelled() {
            count = 4
        }

        override fun performOnce(tpf: Double) {
            isPerformed = true
        }
    }
}