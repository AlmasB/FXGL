/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.ecs.Entity
import com.almasb.fxgl.ecs.EntityWorldListener
import com.almasb.fxgl.entity.GameWorld
import com.almasb.fxgl.gameplay.GameState
import com.almasb.fxgl.physics.PhysicsWorld
import com.almasb.fxgl.scene.GameScene
import com.google.inject.Inject
import com.google.inject.Singleton
import javafx.scene.input.KeyEvent

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Singleton
internal class PlayState
@Inject
private constructor(val gameState: GameState,
                    val gameWorld: GameWorld,
                    val physicsWorld: PhysicsWorld) : AppState(FXGL.getApp().sceneFactory.newGameScene()) {

    init {
        gameWorld.addWorldListener(physicsWorld)
        gameWorld.addWorldListener(gameScene)

        gameWorld.addWorldListener(object : EntityWorldListener {
            override fun onEntityAdded(entity: Entity) {}

            override fun onEntityRemoved(entity: Entity) {}

            override fun onWorldUpdate(tpf: Double) {}

            override fun onWorldReset() {
                timer.clear()
            }
        })

        if (FXGL.getSettings().isMenuEnabled)
            scene.addEventHandler(KeyEvent.ANY, FXGL.getApp().menuListener as MenuEventHandler)
    }

    override fun onUpdate(tpf: Double) {
        gameWorld.onUpdate(tpf)
    }

    val gameScene: GameScene
        get() = scene as GameScene
}