/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.time

import javafx.util.Duration

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface LocalTimer {
    /**
     * Captures current time.
     */
    fun capture()

    /**
     * Returns true if difference between captured time
     * and now is greater or equal to given duration.
     *
     * @param duration time duration to check
     * @return true if elapsed, false otherwise
     */
    fun elapsed(duration: Duration): Boolean
}