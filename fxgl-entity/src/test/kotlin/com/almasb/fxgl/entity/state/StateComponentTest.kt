/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.entity.state

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class StateComponentTest {

    private lateinit var stateComponent: StateComponent

    @BeforeEach
    fun setUp() {
        stateComponent = StateComponent()
    }

    @Test
    fun `State component does not require component injection`() {
        assertFalse(stateComponent.isComponentInjectionRequired)
    }

    @Test
    fun `EntityState does not allow concurrency`() {
        assertFalse(EntityState.IDLE.isAllowConcurrency)
    }

    @Test
    fun `Change state does not set new state if current state equals new`() {
        var count = 0

        val state = object : EntityState() {
            override fun onEnteredFrom(prevState: EntityState?) {
                count++
            }
        }

        stateComponent.changeState(state)
        assertThat(count, `is`(1))

        // does not change state since we are in [state]
        stateComponent.changeState(state)
        assertThat(count, `is`(1))
    }

    @Test
    fun `Change state allowReentry allows reentry into current state`() {
        var count = 0

        val state = object : EntityState() {
            override fun onEnteredFrom(prevState: EntityState?) {
                count++
            }
        }

        stateComponent.changeStateAllowReentry(state)
        assertThat(count, `is`(1))

        // changes state since we allow reentry
        stateComponent.changeStateAllowReentry(state)
        assertThat(count, `is`(2))
    }

    @Test
    fun `State component correctly manages state transitions`() {
        assertTrue(stateComponent.isIdle)
        assertTrue(stateComponent.isIn(EntityState.IDLE))

        val state = object : EntityState() { }

        stateComponent.changeState(state)

        assertFalse(stateComponent.isIdle)
        assertFalse(stateComponent.isIn(EntityState.IDLE))

        assertThat(stateComponent.currentStateProperty().get(), `is`<EntityState>(state))
        assertThat(stateComponent.currentState, `is`<EntityState>(state))
        assertTrue(stateComponent.isIn(state))

        stateComponent.changeStateToIdle()

        assertTrue(stateComponent.isIdle)
        assertTrue(stateComponent.isIn(EntityState.IDLE))
    }

    @Test
    fun `On update`() {
        var count = 0.0

        val state = object : EntityState() {
            override fun onUpdate(tpf: Double) {
                count = tpf
            }
        }

        stateComponent.onUpdate(1.0)
        stateComponent.changeState(state)
        assertThat(count, `is`(0.0))

        stateComponent.onUpdate(1.0)
        assertThat(count, `is`(1.0))
    }

    @Test
    fun `Entity state toString`() {
        val state1 = object : EntityState() { }
        val state2 = object : EntityState("StateName") { }

        assertThat(state1.toString(), containsString(state1.javaClass.name))
        assertThat(state2.toString(), `is`("StateName"))
    }
}