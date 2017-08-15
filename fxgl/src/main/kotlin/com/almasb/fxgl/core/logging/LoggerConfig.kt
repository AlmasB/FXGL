package com.almasb.fxgl.core.logging

import java.time.format.DateTimeFormatter

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class LoggerConfig {

    var dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
    var messageFormatter = DefaultMessageFormatter()

    fun copy(): LoggerConfig {
        val copy = LoggerConfig()
        copy.dateTimeFormatter = dateTimeFormatter
        copy.messageFormatter = messageFormatter
        return copy
    }
}