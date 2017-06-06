/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s06gameplay.levelparsing

import com.almasb.fxgl.app.ApplicationMode
import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.entity.Entities
import com.almasb.fxgl.entity.EntitySpawner
import com.almasb.fxgl.parser.text.TextLevelParser
import com.almasb.fxgl.settings.GameSettings
import javafx.application.Application
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

/**
 * This is an example of a basic FXGL game application in Kotlin.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 */
class LevelParsingSampleKt : GameApplication() {

    private val BLOCK_SIZE = 200.0

    override fun initSettings(settings: GameSettings) {

        with(settings) {
            width = 800
            height = 600
            title = "LevelParsingSampleKt"
            version = "0.1"
            isFullScreen = false
            isIntroEnabled = false
            isMenuEnabled = false
            setProfilingEnabled(true)
            applicationMode = ApplicationMode.DEVELOPER
        }
    }

    override fun initInput() { }

    override fun initAssets() { }

    override fun initGame() {
        val parser = TextLevelParser('0', BLOCK_SIZE.toInt(), BLOCK_SIZE.toInt())

        parser.addEntityProducer('1', EntitySpawner { Entities.builder()
                .at(it.x, it.y)
                .viewFromNode(Rectangle(BLOCK_SIZE, BLOCK_SIZE, Color.RED))
                .build() })

        parser.addEntityProducer('2', EntitySpawner { Entities.builder()
                .at(it.x, it.y)
                .viewFromNode(Rectangle(BLOCK_SIZE, BLOCK_SIZE, Color.GREEN))
                .build() })

        val level = parser.parse("level0.txt")

        gameWorld.setLevel(level)
    }

    override fun initPhysics() { }

    override fun initUI() { }

    override fun onUpdate(tpf: Double) { }
}

fun main(args: Array<String>) {
    Application.launch(LevelParsingSampleKt::class.java, *args)
}