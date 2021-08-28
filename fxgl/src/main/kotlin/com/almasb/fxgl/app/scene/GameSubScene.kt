/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.scene

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.entity.GameWorld
import com.almasb.fxgl.physics.PhysicsWorld
import com.almasb.fxgl.scene.SubScene

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
open class GameSubScene
@JvmOverloads constructor(
        width: Int,
        height: Int,
        is3D: Boolean = false
) : SubScene() {

    val gameScene = GameScene(
            width,
            height,
            GameWorld(),
            PhysicsWorld(height, FXGL.getSettings().pixelsPerMeter),
            is3D
    )

    val gameWorld: GameWorld
        get() = gameScene.gameWorld

    val physicsWorld: PhysicsWorld
        get() = gameScene.physicsWorld

    init {
        contentRoot.children += gameScene.root
    }

    override fun onUpdate(tpf: Double) {
        gameScene.update(tpf)
    }
}