/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.logging

/**
 * Allows users to re-route logger messages to anywhere.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface LoggerOutput {

    /**
     * Called when a new message is being logged.
     */
    fun append(message: String)

    /**
     * Called to allow this output to clean up / serialize / shut down.
     */
    fun close()
}