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

package com.almasb.fxgl.app.state

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.MenuEventHandler
import com.almasb.fxgl.entity.GameWorld
import com.almasb.fxgl.gameplay.GameState
import com.almasb.fxgl.physics.PhysicsWorld
import com.almasb.fxgl.scene.GameScene
import com.almasb.fxgl.time.UpdateEvent
import javafx.scene.input.KeyEvent

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object PlayState : AbstractAppState(FXGL.getInstance(GameScene::class.java)) {

    val gameState = FXGL.getInstance(GameState::class.java)
    val gameWorld = FXGL.getInstance(GameWorld::class.java)
    val physicsWorld = FXGL.getInstance(PhysicsWorld::class.java)

    init {
        gameWorld.addWorldListener(physicsWorld)
        gameWorld.addWorldListener(scene as GameScene)

        if (FXGL.getSettings().isMenuEnabled)
            scene.addEventHandler(KeyEvent.ANY, FXGL.getApp().menuListener as MenuEventHandler)
    }

    override fun onEnter(prevState: State) {

    }

    override fun onExit() {

    }

    override fun onUpdate(tpf: Double) {
        // TODO: hardcoded event
        gameWorld.onUpdateEvent(UpdateEvent(0, tpf))
        FXGL.getApp().update(tpf)
    }
}