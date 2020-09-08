/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.logging

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DefaultMessageFormatter : MessageFormatter {

    override fun makeMessage(dateTime: String, threadName: String, loggerLevel: String, loggerName: String, loggerMessage: String): String {
        return "%s [%-25.25s] %-5.5s %-20.20s - %s".format(dateTime, threadName, loggerLevel, loggerName, loggerMessage)
    }
}