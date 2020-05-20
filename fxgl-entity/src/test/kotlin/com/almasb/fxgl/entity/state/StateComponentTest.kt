/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.state

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
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
    fun `State component correctly manages state transitions`() {
        assertTrue(stateComponent.isIdle)
        assertTrue(stateComponent.isIn(EntityState.IDLE))

        val state = object : EntityState() { }

        stateComponent.changeState(state)

        assertFalse(stateComponent.isIdle)
        assertFalse(stateComponent.isIn(EntityState.IDLE))

        assertThat(stateComponent.currentState, `is`<EntityState>(state))
        assertTrue(stateComponent.isIn(state))

        stateComponent.changeStateToIdle()

        assertTrue(stateComponent.isIdle)
        assertTrue(stateComponent.isIn(EntityState.IDLE))
    }

    @Test
    fun `Entity state toString`() {
        val state1 = object : EntityState() { }
        val state2 = object : EntityState("StateName") { }

        assertThat(state1.toString(), containsString(state1.javaClass.name))
        assertThat(state2.toString(), `is`("StateName"))
    }
}