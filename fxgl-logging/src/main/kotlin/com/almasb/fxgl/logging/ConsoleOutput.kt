/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.logging

/**
 * Logger output that sends all messages to System.out via println().
 * Closing this output is a no-op.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ConsoleOutput : LoggerOutput {

    override fun append(message: String) {
        println(message)
    }

    override fun close() {}
}