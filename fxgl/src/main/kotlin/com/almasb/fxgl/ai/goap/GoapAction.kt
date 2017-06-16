/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.goap

import com.almasb.fxgl.ecs.Entity

/**
 * Adapted from https://github.com/sploreg/goap
 * Original source: C#, author: Brent Anthony Owens.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class GoapAction {

    val preconditions = State()
    val effects = State()

    /**
     * Are we in range of the target?
     * The MoveTo state will set this and it gets reset each time this action is performed.
     */
    var isInRange = false

    /**
     * The cost of performing the action.
     * Figure out a weight that suits the action.
     * Changing it will affect what actions are chosen during planning.
     */
    var cost = 1f

    /**
     * An action often has to perform on an object.
     * This is that object.
     * Can be null.
     */
    var target: Entity? = null

    fun doReset() {
        isInRange = false
        target = null
        reset()
    }

    /**
     * Reset any variables that need to be reset before planning happens again.
     */
    abstract fun reset()

    /**
     * Is the action done?
     */
    abstract val isDone: Boolean

    /**
     * Procedurally check if this action can run. Not all actions
     * will need this, but some might.
     */
    abstract fun checkProceduralPrecondition(agent: Entity): Boolean

    /**
     * Run the action.
     * Returns True if the action performed successfully or false
     * if something happened and it can no longer perform. In this case
     * the action queue should clear out and the goal cannot be reached.
     */
    abstract fun perform(agent: Entity): Boolean

    /**
     * Does this action need to be within range of a target game object?
     * If not then the moveTo state will not need to run for this action.
     */
    abstract fun requiresInRange(): Boolean

    fun addPrecondition(key: String, value: Any) {
        preconditions.add(key, value)
    }

    fun removePrecondition(key: String) {
        preconditions.remove(key)
    }

    fun addEffect(key: String, value: Any) {
        effects.add(key, value)
    }

    fun removeEffect(key: String) {
        effects.remove(key)
    }

    fun getName(): String {
        return javaClass.simpleName
    }

    override fun toString(): String {
        return getName()
    }
}
