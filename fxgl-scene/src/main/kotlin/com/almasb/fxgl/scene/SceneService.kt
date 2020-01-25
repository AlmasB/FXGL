/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.time.Timer
import javafx.scene.Group

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class SceneService : EngineService() {

    /**
     * The root for the overlay group that is constantly visible and on top
     * of every other UI element. For things like notifications.
     */
    abstract val overlayRoot: Group

    abstract val appWidth: Int

    abstract val appHeight: Int

    /**
     * Always-on timer.
     */
    abstract val timer: Timer

    abstract fun pushSubScene(subScene: SubScene)

    abstract fun popSubScene()
}