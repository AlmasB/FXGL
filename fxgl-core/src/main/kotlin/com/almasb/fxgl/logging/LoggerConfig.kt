/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.logging

import java.time.format.DateTimeFormatter

/**
 * Config data structure that allows setting custom date-time and message formatters.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class LoggerConfig {

    var dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
    var messageFormatter = DefaultMessageFormatter()

    internal fun copy(): LoggerConfig {
        val copy = LoggerConfig()
        copy.dateTimeFormatter = dateTimeFormatter
        copy.messageFormatter = messageFormatter
        return copy
    }
}