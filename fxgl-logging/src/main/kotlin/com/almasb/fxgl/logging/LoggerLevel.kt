package com.almasb.fxgl.logging

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
enum class LoggerLevel {
    DEBUG,
    INFO,
    WARNING {
        override fun toString(): String {
            return "WARN"
        }
    },
    FATAL
}