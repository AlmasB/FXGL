/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.core.reflect.ReflectionUtils.callInaccessible
import com.almasb.fxgl.core.reflect.ReflectionUtils.getMethod
import com.almasb.fxgl.dsl.FXGL

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FXGLMock {

    companion object {
        @JvmStatic fun mock() {
            val app = MockGameApplication.get()

            val engine = Engine(app, app.settings, app.stage)

            FXGL.inject(engine)
            //callInaccessible<Any>(FXGL, getMethod(FXGL.javaClass, "inject", Engine::class.java), engine)
        }
    }
}