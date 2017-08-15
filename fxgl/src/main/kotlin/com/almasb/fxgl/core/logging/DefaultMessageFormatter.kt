package com.almasb.fxgl.core.logging

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DefaultMessageFormatter : MessageFormatter {

    override fun makeMessage(dateTime: String, threadName: String, loggerLevel: String, loggerName: String, loggerMessage: String): String {
        return "%s [%-25s] %-5s %-20s - %s".format(dateTime, threadName, loggerLevel, loggerName, loggerMessage)
    }
}