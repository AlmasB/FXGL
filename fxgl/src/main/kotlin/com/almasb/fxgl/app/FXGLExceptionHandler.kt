/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.service.ExceptionHandler
import com.google.inject.Inject

/**
 * Default exception handler service provider.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FXGLExceptionHandler
@Inject private constructor() : ExceptionHandler {

    private val log = Logger.get(javaClass)

    override fun handle(e: Throwable) {
        log.warning("Caught Exception: $e")
        FXGL.getDisplay().showErrorBox(e)
    }
}