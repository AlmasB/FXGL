/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.fsm

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class StateMachineTest {

    private lateinit var initialState: TestState
    private lateinit var state1: TestState
    private lateinit var state2: TestState
    private lateinit var subState1: TestState
    private lateinit var subState2: TestState
    private lateinit var machine: StateMachine<TestState>

    @BeforeEach
    fun setUp() {
        initialState = TestState(false, false)
        machine = StateMachine(initialState)
        state1 = TestState(false, false)
        state2 = TestState(false, false)
        subState1 = TestState(true, false)
        subState2 = TestState(true, false)
    }

    @Test
    fun `Fails when initial state is a substate`() {
        assertThrows(IllegalArgumentException::class.java) {
            StateMachine(TestState(true, false))
        }
    }

    @Test
    fun `Change parent state to new state`() {
        machine.changeState(state1)

        assertThat(machine.parentState, `is`(machine.currentState))
        assertThat(machine.currentState, `is`(state1))

        machine.changeState(state2)

        assertThat(machine.parentState, `is`(machine.currentState))
        assertThat(machine.currentState, `is`(state2))
    }

    @Test
    fun `Change state to new substate`() {
        machine.changeState(subState1)

        assertThat(machine.parentState, `is`(initialState))
        assertThat(machine.currentState, `is`(subState1))

        machine.changeState(subState2)

        assertThat(machine.parentState, `is`(initialState))
        assertThat(machine.currentState, `is`(subState2))

        machine.popSubState()

        assertThat(machine.parentState, `is`(initialState))
        assertThat(machine.currentState, `is`(subState1))
    }

    @Test
    fun `Allow substate concurrency`() {
        val runResult = arrayListOf<TestState>()

        assertThat(machine.activeStates, contains(initialState))

        machine.changeState(subState1)

        assertThat(machine.activeStates, contains(subState1))

        machine.runOnActiveStates { runResult += it }
        assertThat(runResult, contains(subState1))

        val subState3 = TestState(true, true)

        machine.changeState(subState3)

        assertThat(machine.activeStates, contains(subState1, subState3))
        runResult.clear()
        machine.runOnActiveStates { runResult += it }
        assertThat(runResult, contains(subState1, subState3))

        machine.changeState(subState2)

        assertThat(machine.activeStates, contains(subState2))

        machine.popSubState()

        assertThat(machine.activeStates, contains(subState1, subState3))
    }

    @Test
    fun `Parent state is also active when substate allows concurrency`() {
        val subState3 = TestState(true, true)

        machine.changeState(subState3)

        assertThat(machine.activeStates, contains(initialState, subState3))
    }

    @Test
    fun `New parent state is not set if substates are present`() {
        machine.changeState(subState1)

        machine.changeState(state2)

        assertThat(machine.parentState, `is`(initialState))
    }

    @Test
    fun `State is not popped if there are none`() {
        assertFalse(machine.popSubState())
    }

    @Test
    fun `Test lifecycle callbacks`() {
        // parent -> substate
        machine.changeState(subState1)

        assertThat(subState1.create, `is`(1))
        assertThat(subState1.enter, `is`(1))
        assertThat(subState1.exit, `is`(0))
        assertThat(subState1.destroy, `is`(0))

        assertThat(initialState.exit, `is`(1))

        assertThat(subState1.enteredFrom, `is`<State<*>>(initialState))
        assertThat(initialState.exitedTo, `is`<State<*>>(subState1))


        // substate -> substate
        machine.changeState(subState2)

        assertThat(subState1.create, `is`(1))
        assertThat(subState1.enter, `is`(1))
        assertThat(subState1.exit, `is`(1))
        assertThat(subState1.destroy, `is`(0))

        assertThat(subState2.create, `is`(1))
        assertThat(subState2.enter, `is`(1))
        assertThat(subState2.exit, `is`(0))
        assertThat(subState2.destroy, `is`(0))

        assertThat(subState1.exitedTo, `is`<State<*>>(subState2))
        assertThat(subState2.enteredFrom, `is`<State<*>>(subState1))

        // substate <- substate
        machine.popSubState()

        assertThat(subState1.create, `is`(1))
        assertThat(subState1.enter, `is`(2))
        assertThat(subState1.exit, `is`(1))
        assertThat(subState1.destroy, `is`(0))

        assertThat(subState2.create, `is`(1))
        assertThat(subState2.enter, `is`(1))
        assertThat(subState2.exit, `is`(1))
        assertThat(subState2.destroy, `is`(1))

        assertThat(subState2.exitedTo, `is`<State<*>>(subState1))
        assertThat(subState1.enteredFrom, `is`<State<*>>(subState2))


        // parent <- substate
        machine.popSubState()

        assertThat(subState1.create, `is`(1))
        assertThat(subState1.enter, `is`(2))
        assertThat(subState1.exit, `is`(2))
        assertThat(subState1.destroy, `is`(1))

        assertThat(subState1.exitedTo, `is`<State<*>>(initialState))
        assertThat(initialState.enteredFrom, `is`<State<*>>(subState1))


        // parent -> parent
        machine.changeState(state2)

        assertThat(initialState.create, `is`(1))
        assertThat(initialState.enter, `is`(1))
        assertThat(initialState.exit, `is`(2))
        assertThat(initialState.destroy, `is`(1))

        assertThat(initialState.exitedTo, `is`<State<*>>(state2))
        assertThat(state2.enteredFrom, `is`<State<*>>(initialState))
    }

    private class TestState(override val isSubState: Boolean, override val isAllowConcurrency: Boolean) : State<TestState> {
        var create = 0
        var destroy = 0
        var enter = 0
        var exit = 0

        lateinit var enteredFrom: TestState
        lateinit var exitedTo: TestState

        override fun onCreate() {
            create++
        }

        override fun onDestroy() {
            destroy++
        }

        override fun onEnteredFrom(prevState: TestState) {
            enteredFrom = prevState
            enter++
        }

        override fun onExitingTo(nextState: TestState) {
            exitedTo = nextState
            exit++
        }
    }
}