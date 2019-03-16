/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.GameSettings
import com.almasb.fxgl.dsl.addUINode
import com.almasb.fxgl.dsl.getGameWorld
import com.almasb.fxgl.entity.Entity
import javafx.scene.text.Text

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class KotlinGameApp : GameApplication() {
    override fun initSettings(settings: GameSettings) {
        with(settings) {
            width = 720
            height = 640
            title = "Kotlin Game"
        }
    }

    override fun initGame() {

        getGameWorld().addEntity(Entity())

        addUINode(Text("HELLO WORLD"), 100.0, 200.0)
    }
}

fun main() {
    GameApplication.launch(KotlinGameApp::class.java, emptyArray())
}