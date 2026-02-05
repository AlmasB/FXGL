/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.goap

import com.almasb.fxgl.core.collection.PropertyMap

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
open class GoapAction
@JvmOverloads constructor(
    var name: String = ""
) {

    /**
     * Predicates that need to be true for this action to run.
     */
    val preconditions = PropertyMap()

    /**
     * Results that are true after this action successfully completed.
     */
    val effects = PropertyMap()

    /**
     * The cost of performing the action.
     * Actions with the total lowest cost are chosen during planning.
     */
    var cost = 1f

    fun addPrecondition(key: String, value: Any) {
        preconditions.setValue(key, value)
    }

    fun removePrecondition(key: String) {
        preconditions.remove(key)
    }

    fun addEffect(key: String, value: Any) {
        effects.setValue(key, value)
    }

    fun removeEffect(key: String) {
        effects.remove(key)
    }

    override fun toString(): String {
        return name
    }
}

//
//    /**
//     * An action often has to perform on an object.
//     * This is that object. Can be null.
//     */
//    var target: Entity? = null
//
//    /**
//     * Check if this action can run.
//     * TODO: is available, rather than can run.
//     */
//    open fun canRun() = true
//
//    override fun onUpdate(tpf: Double) {
//        // TODO: perform(tpf)
//        perform()
//    }
//
//    /**
//     * Perform the action.
//     */
//    abstract fun perform()