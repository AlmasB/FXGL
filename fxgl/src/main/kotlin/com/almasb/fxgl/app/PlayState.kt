/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.ecs.GameWorld
import com.almasb.fxgl.gameplay.GameState
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.physics.PhysicsWorld
import com.almasb.fxgl.scene.GameScene
import com.almasb.fxgl.scene.SceneFactory
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
                    val physicsWorld: PhysicsWorld,
                    sceneFactory: SceneFactory) : AppState(sceneFactory.newGameScene()) {

    init {
        gameWorld.addWorldListener(physicsWorld)
        gameWorld.addWorldListener(gameScene)

        if (FXGL.getSettings().isMenuEnabled) {
            input.addEventHandler(KeyEvent.ANY, FXGL.getApp().menuListener as MenuEventHandler)
        } else {
            input.addAction(object : UserAction("Pause") {
                override fun onActionBegin() {
                    PauseMenuSubState.requestShow()
                }

                override fun onActionEnd() {
                    PauseMenuSubState.unlockSwitch()
                }
            }, FXGL.getSettings().menuKey)
        }
    }

    override fun onUpdate(tpf: Double) {
        gameWorld.onUpdate(tpf)
        physicsWorld.onUpdate(tpf)
        gameScene.onUpdate(tpf)
    }

    val gameScene: GameScene
        get() = scene as GameScene
}