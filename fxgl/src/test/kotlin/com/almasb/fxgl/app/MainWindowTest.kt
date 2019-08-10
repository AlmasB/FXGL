/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.core.concurrent.Async
import javafx.scene.Parent
import javafx.stage.Stage
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import java.nio.file.Files
import java.nio.file.Paths

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class MainWindowTest {

    companion object {

        private lateinit var window: MainWindow
        private lateinit var stage: Stage
        private lateinit var scene: FXGLScene

        private const val WIDTH = 600
        private const val HEIGHT = 400

        @BeforeAll
        @JvmStatic fun before() {
            FXGLMock.mock()

            Files.deleteIfExists(Paths.get("testScreen.png"))

            Async.startFX {

                val settings = GameSettings()
                settings.width = WIDTH
                settings.height = HEIGHT

                stage = MockGameApplication.get().stage
                scene = object : FXGLScene() {}

                window = MainWindow(stage, scene, settings.toReadOnly())
            }.await()
        }
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun runTests() {
        var count = 0

        Async.startFX {

            `Show Window`()
            `Set scene`()
            `Take screenshot`()
            `Save screenshot`()

            count++
        }.await()

        assertThat(count, `is`(1))
    }

    fun `Show Window`() {
        window.show()

        assertThat(window.stage, `is`(stage))

        assertTrue(stage.isShowing, "Window is not showing")
        assertTrue(stage.width >= WIDTH, "Window is not at least $WIDTH wide")
        assertTrue(stage.height >= HEIGHT, "Window is not at least $HEIGHT high")

        assertThat(stage.scene.root, `is`<Parent>(scene.root))
    }

    fun `Set scene`() {
        val scene2 = object : FXGLScene() {}

        window.setScene(scene2)

        assertThat(stage.scene.root, `is`<Parent>(scene2.root))
    }

    fun `Take screenshot`() {
        val img = window.takeScreenshot()

        assertThat(img.width, `is`(WIDTH.toDouble()))
        assertThat(img.height, `is`(HEIGHT.toDouble()))
    }

    fun `Save screenshot`() {
        val ok = window.saveScreenshot("testScreen.png")

        assertTrue(ok, "Failed to save screenshot")

        Files.deleteIfExists(Paths.get("testScreen.png"))
    }
}