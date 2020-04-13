/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

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