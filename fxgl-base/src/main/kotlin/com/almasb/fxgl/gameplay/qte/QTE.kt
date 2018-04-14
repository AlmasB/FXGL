/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.qte

import com.almasb.fxgl.util.Consumer
import javafx.scene.input.KeyCode
import javafx.util.Duration

/**
 * Quick Time Events.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface QTE {

    /**
     * Starts quick time event.
     * Game execution is blocked during the event.
     * The event can be finishes if one of the following conditions is met:
     *
     *  * User runs out of time (fail)
     *  * User presses the wrong key (fail)
     *  * User correctly presses all keys (success)
     *
     * @param callback called with true if user succeeds in the event, false otherwise
     * @param duration how long the event should last
     * @param keys what keys need to be pressed
     */
    fun start(callback: Consumer<Boolean>, duration: Duration, vararg keys: KeyCode)
}