/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.event.EventBus
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.time.Timer
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.scene.Group
import java.util.Optional

/**
 * Provides access to pushing / popping subscene stack, global input, timer, overlay root and application
 * preferred width / height to lower-level modules.
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
     * @return preferred width (depends on auto-scaling) of the active scene at the time of call
     */
    val prefWidth: Double
        get() = prefWidthProperty().value

    /**
     * @return preferred height (depends on auto-scaling) of the active scene at the time of call
     */
    val prefHeight: Double
        get() = prefHeightProperty().value

    /**
     * @return a convenience property that auto-sets to target (app) width if auto-scaling is enabled
     * and uses actual javafx scene width if not
     */
    abstract fun prefWidthProperty(): ReadOnlyDoubleProperty

    /**
     * @return a convenience property that auto-sets to target (app) height if auto-scaling is enabled
     * and uses actual javafx scene height if not
     */
    abstract fun prefHeightProperty(): ReadOnlyDoubleProperty

    /**
     * Global event bus.
     */
    abstract val eventBus: EventBus

    /**
     * Always-on input.
     */
    abstract val input: Input

    /**
     * Always-on timer.
     */
    abstract val timer: Timer

    /**
     * Global world properties.
     */
    abstract val worldProperties: PropertyMap

    /**
     * @return top-most scene (or subscene) in the scene service hierarchy
     */
    abstract val currentScene: Scene

    /**
     * @return intro scene constructed for this game if present
     */
    abstract val introScene: Optional<Scene>

    /**
     * @return loading scene constructed for this game if present
     */
    abstract val loadingScene: Optional<Scene>

    /**
     * @return game scene constructed for this game
     */
    abstract val gameScene: Scene

    /**
     * @return main menu scene constructed for this game if present
     */
    abstract val mainMenuScene: Optional<Scene>

    /**
     * @return game menu scene constructed for this game if present
     */
    abstract val gameMenuScene: Optional<Scene>

    /**
     * @return true if [scene] is in this scene service hierarchy
     */
    abstract fun isInHierarchy(scene: Scene): Boolean

    /**
     * Push a given [subScene] on top of current scene or subscene.
     */
    abstract fun pushSubScene(subScene: SubScene)

    /**
     * Pop current subscene.
     * Only use this to pop your own (previously pushed) subscene.
     */
    abstract fun popSubScene()
}