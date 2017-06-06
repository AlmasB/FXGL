/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.logging

import java.util.function.Supplier

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object SystemLogger : Logger {

    override fun info(message: String) = println("INFO: $message")

    override fun info(messageSupplier: Supplier<String>) = info(messageSupplier.get())

    override fun debug(message: String) = println("DEBUG: $message")

    override fun debug(messageSupplier: Supplier<String>) = debug(messageSupplier.get())

    override fun warning(message: String) = println("WARN: $message")

    override fun warning(messageSupplier: Supplier<String>) = warning(messageSupplier.get())

    override fun fatal(message: String) = println("FATAL: $message")

    override fun fatal(messageSupplier: Supplier<String>) = fatal(messageSupplier.get())

    override fun close() {
    }
}