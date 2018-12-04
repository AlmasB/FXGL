/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.core.util.BiConsumer
import com.almasb.fxgl.core.util.forEach
import com.almasb.fxgl.entity.GameWorld
import com.almasb.fxgl.event.Subscriber
import com.almasb.fxgl.gameplay.GameState
import com.almasb.fxgl.physics.PhysicsWorld
import com.almasb.fxgl.saving.DataFile
import com.almasb.fxgl.scene.*
import com.almasb.fxgl.scene.intro.IntroFinishedEvent
import com.almasb.sslogger.Logger
import javafx.concurrent.Task
import javafx.event.EventHandler

/**
 * All app states.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */


/**
 * State is active during game initialization.
 */
