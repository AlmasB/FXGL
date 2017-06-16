/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.goap

import com.almasb.fxgl.ecs.Entity
import java.util.*

/**
 * Stack-based Finite State Machine.
 * Push and pop states to the FSM.
 *
 * States should push other states onto the stack
 * and pop themselves off.
 *
 * Adapted from https://github.com/sploreg/goap
 * Original source: C#, author: Brent Anthony Owens.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FSM {

    private val stateStack = ArrayDeque<FSMState>()

    fun update(entity: Entity) {
        if (stateStack.peek() != null)
            stateStack.peek().update(this, entity)
    }

    fun pushState(state: FSMState) {
        stateStack.push(state)
    }

    fun popState() {
        stateStack.pop()
    }
}