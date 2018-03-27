/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

/**
 * Default exception handler.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FXGLExceptionHandler : ExceptionHandler {

    override fun accept(e: Throwable) {
        FXGL.getDisplay().showErrorBox(e)
    }
}