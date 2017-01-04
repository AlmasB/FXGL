package com.almasb.easyio

import org.apache.logging.log4j.LogManager
import java.util.concurrent.Executor
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * Instance of EasyIO configuration.
 * Allows to set defaults to simplify IOTask construction and usage.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object EasyIO {

    private val log = LogManager.getLogger(EasyIO::class.java)

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