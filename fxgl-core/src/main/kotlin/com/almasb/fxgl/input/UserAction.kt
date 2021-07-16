/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input

/**
 * Represents a user action which is typically triggered when a key
 * or a mouse event has occurred. User actions have unique names so that they
 * are easily identifiable. An action can be bound to a key or mouse event
 * using Input.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
abstract class UserAction(

        /**
         * Constructs new user action with given name. Name examples:
         * Walk Forward, Shoot, Use, Aim, etc.
         */
        val name: String) {

    override fun equals(other: Any?): Boolean {
        return other is UserAction && name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return name
    }

    internal fun begin() = onActionBegin()
    internal fun action() = onAction()
    internal fun end() = onActionEnd()

    /**
     * Called once in the same tick when triggered.
     */
    protected open fun onActionBegin() {}

    /**
     * Called as long as the trigger is being held (pressed).
     * Starts from the next tick from the one when was triggered.
     */
    protected open fun onAction() {}

    /**
     * Called once in the same tick when trigger was released.
     */
    protected open fun onActionEnd() {}
}