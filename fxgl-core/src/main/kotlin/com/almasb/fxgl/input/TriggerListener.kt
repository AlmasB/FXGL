/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class TriggerListener {

    internal fun begin(trigger: Trigger) = onActionBegin(trigger)
    internal fun action(trigger: Trigger) = onAction(trigger)
    internal fun end(trigger: Trigger) = onActionEnd(trigger)

    /**
     * Called once in the same tick when triggered.
     */
    protected open fun onActionBegin(trigger: Trigger) {}

    /**
     * Called as long as the trigger is being held (pressed).
     * Starts from the next tick from the one when was triggered.
     */
    protected open fun onAction(trigger: Trigger) {}

    /**
     * Called once in the same tick when trigger was released.
     */
    protected open fun onActionEnd(trigger: Trigger) {}
}