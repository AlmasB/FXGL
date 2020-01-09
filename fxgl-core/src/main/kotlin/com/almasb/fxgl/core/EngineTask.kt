/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class EngineTask : EngineService() {

    var isCompleted = false
        protected set

    abstract fun onEngineInit()
}