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

    /**
     * For messages of very specific fine-grained nature that are typically used
     * for trace and debugging purposes.
     * These messages do not appear in normal development mode to avoid extra noise.
     */
    DEBUG,

    /**
     * For messages that are useful to the developer during normal run.
     */
    INFO,

    /**
     * For messages that indicate that something unexpected or erroneous has happened,
     * but we were able to manage it and we are (probably) not going to crash.
     */
    WARN,

    /**
     * For messages of highest severity, to be used only for crashes.
     */
    FATAL
}