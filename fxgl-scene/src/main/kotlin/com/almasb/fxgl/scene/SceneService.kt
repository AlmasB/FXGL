/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.time.Timer
import javafx.beans.property.ReadOnlyDoubleProperty
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

    /**
     * @return target app width, [prefWidthProperty] is preferred
     */
    abstract val appWidth: Int

    /**
     * @return target app height, [prefHeightProperty] is preferred
     */
    abstract val appHeight: Int

    abstract fun prefWidthProperty(): ReadOnlyDoubleProperty

    abstract fun prefHeightProperty(): ReadOnlyDoubleProperty

    /**
     * Always-on input.
     */
    abstract val input: Input

    /**
     * Always-on timer.
     */
    abstract val timer: Timer

    abstract fun pushSubScene(subScene: SubScene)

    abstract fun popSubScene()
}