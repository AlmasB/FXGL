/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.io

import org.apache.logging.log4j.LogManager
import java.util.concurrent.Executor
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * Instance of FXGLIO configuration.
 * Allows to set defaults to simplify IOTask construction and usage.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object FXGLIO {

    private val log = LogManager.getLogger(FXGLIO::class.java)

    /**
     * Default exception handler that simply logs the exception
     */
    var defaultExceptionHandler = Consumer<Throwable> { log.warn("Exception occurred: $it") }

    /**
     * Default executor that uses the same thread as the caller.
     */
    var defaultExecutor = Executor { it.run() }

    /**
     * Default UI dialog supplier that does nothing.
     */
    var defaultUIDialogSupplier = Supplier<UIDialogHandler> { object : UIDialogHandler {

            override fun dismiss() {
                // no-op
            }

            override fun show() {
                // no-op
            }
        }
    }
}