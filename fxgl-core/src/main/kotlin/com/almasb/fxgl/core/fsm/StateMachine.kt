/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.fsm

import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * This state machine always has a present single state.
 * There can be [0..*] substates.
 * A state cannot be changed if there are active substates.
 *
 * A example view of the hierarchy: parent -> (substate1) -> (substate2)
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class StateMachine<S : State<S>>(initialState: S) {

    /**
     * A (sub)state is active when it is parent, or when it allows concurrency
     * and is a substate of parent.
     */
    val activeStates = CopyOnWriteArrayList<S>()

    /**
     * A queue of substates.
     * The queue starts with the first (top) element and ends with the last element.
     * The last element (if present) is the current state.
     */
    private val subStates = ArrayDeque<S>()

    /**
     * A current non-substate state.
     */
    var parentState: S = initialState
        private set(value) {
            require(!value.isSubState) {
                "Parent state $value cannot be a substate"
            }

            field = value
        }

    val currentState: S
        get() = if (subStates.isNotEmpty()) subStates.last else parentState

    init {
        parentState = initialState

        initialState.onCreate()

        activeStates += parentState
    }

    fun runOnActiveStates(action: (S) -> Unit) {
        activeStates.forEach { action(it) }
    }

    fun changeState(newState: S) {
        if (subStates.isEmpty()) {

            if (newState.isSubState) {
                // moving parent -> substate

                val prevState = currentState

                newState.onCreate()
                prevState.onExitingTo(newState)
                subStates.addLast(newState)
                newState.onEnteredFrom(parentState)

            } else {
                // moving parent -> parent

                val prevState = currentState

                newState.onCreate()
                prevState.onExitingTo(newState)
                parentState = newState
                newState.onEnteredFrom(prevState)
                prevState.onDestroy()
            }
        } else {

            if (!newState.isSubState) {

                // moving substate -> parent
                // cannot change parent if substates are present
                return
            } else {

                // moving substate -> substate

                val prevState = currentState

                newState.onCreate()
                prevState.onExitingTo(newState)
                subStates.addLast(newState)
                newState.onEnteredFrom(prevState)
            }
        }

        updateActiveStates()
    }

    /**
     * @return true if state successfully changed
     */
    fun popSubState(): Boolean {
        if (subStates.isEmpty())
            return false

        // moving substate (new) <- substate (prev)
        // OR       parent (new) <- substate (prev)

        val prevState = currentState
        val newState = if (subStates.size == 1) parentState else subStates.elementAt(subStates.size - 2)

        prevState.onExitingTo(newState)
        subStates.removeLast()
        newState.onEnteredFrom(prevState)
        prevState.onDestroy()

        updateActiveStates()

        return true
    }

    private fun updateActiveStates() {
        activeStates.clear()

        if (subStates.isEmpty()) {
            activeStates += parentState
            return
        }

        for (s in subStates.reversed()) {
            if (s.isAllowConcurrency) {
                activeStates += s
            } else {
                activeStates += s
                break
            }
        }

        // add parent only if the first substate allows concurrency
        if (subStates.first.isAllowConcurrency) {
            activeStates += parentState
        }

        activeStates.reverse()
    }
}