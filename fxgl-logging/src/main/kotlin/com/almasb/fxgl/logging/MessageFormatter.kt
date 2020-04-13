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