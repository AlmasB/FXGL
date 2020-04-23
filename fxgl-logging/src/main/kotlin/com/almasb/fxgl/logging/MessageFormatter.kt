/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.logging

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface MessageFormatter {

    fun makeMessage(dateTime: String,
                    threadName: String,
                    loggerLevel: String,
                    loggerName: String,
                    loggerMessage: String): String
}