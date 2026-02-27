/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.action;

public enum CancelPolicy {

    /**
     * Cancels just the action itself.
     */
    ONE,

    /**
     * Cancels all queued actions.
     */
    ALL
}
