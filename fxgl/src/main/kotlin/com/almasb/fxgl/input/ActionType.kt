/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input

/**
 * Action type defines when an action needs to be executed.
 */
enum class ActionType {

    /**
     * Executed once in the same tick when triggered.
     */
    ON_ACTION_BEGIN,

    /**
     * Executed as long as the trigger is being held (pressed).
     * Starts from the next tick from the one when was triggered.
     */
    ON_ACTION,

    /**
     * Executed once in the same tick when trigger was released.
     */
    ON_ACTION_END
}