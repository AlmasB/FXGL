/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FXGLMock {

    companion object {
        @JvmStatic fun mock() {
            val app = MockGameApplication.get()

            FXGL.engine = Engine(app, app.settings, app.stage)
        }
    }
}