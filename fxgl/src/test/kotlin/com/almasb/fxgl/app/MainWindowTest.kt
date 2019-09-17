/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.scene.Scene
import com.almasb.fxgl.scene.SubScene
import com.almasb.fxgl.test.RunWithFX
import javafx.scene.Parent
import javafx.scene.image.Image
import javafx.stage.Stage
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.api.extension.ExtendWith

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@ExtendWith(RunWithFX::class)
class MainWindowTest {

    companion object {

        private lateinit var window: MainWindow
        private lateinit var stage: Stage
        private lateinit var scene: FXGLScene

        private const val WIDTH = 600
        private const val HEIGHT = 400

        @BeforeAll
        @JvmStatic fun before() {
            Async.startAsyncFX {

                val settings = GameSettings()
                settings.width = WIDTH
                settings.height = HEIGHT

                stage = Stage()
                scene = object : FXGLScene(WIDTH, HEIGHT) {}

                window = MainWindow(stage, scene, settings.toReadOnly())
            }.await()
        }
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun runTests() {
        var count = 0

        Async.startAsyncFX {

            `Add icon`()
            `Add CSS`()
            `Show Window`()
            `Set scene`()
            `Take screenshot`()
            `Push and pop subscene`()

            count++
        }.await()

        assertThat(count, `is`(1))
    }

    fun `Add icon`() {
        assertTrue(stage.icons.isEmpty())

        val image = Image(javaClass.getResource("test_icon.png").toExternalForm())

        window.addIcons(image)

        assertTrue(stage.icons.isNotEmpty())
    }

    fun `Add CSS`() {
        assertTrue(scene.root.scene.stylesheets.isEmpty())

        val css = CSS(javaClass.getResource("test.css").toExternalForm())
        window.addCSS(css)

        assertTrue(scene.root.scene.stylesheets.isNotEmpty())
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
        val scene2 = object : FXGLScene(WIDTH, HEIGHT) {}

        window.setScene(scene2)

        assertThat(stage.scene.root, `is`<Parent>(scene2.root))
    }

    fun `Take screenshot`() {
        val img = window.takeScreenshot()

        assertThat(img.width, `is`(WIDTH.toDouble()))
        assertThat(img.height, `is`(HEIGHT.toDouble()))
    }

    fun `Push and pop subscene`() {
        var t = 0.0

        val subscene = object : SubScene() {
            override fun onUpdate(tpf: Double) {
                t += tpf
            }
        }

        window.pushState(subscene)

        assertThat(window.currentScene, `is`<Scene>(subscene))

        window.onUpdate(1.0)

        assertThat(t, `is`(1.0))

        window.popState()

        assertThat(window.currentScene, `is`(not<Scene>(subscene)))

        window.onUpdate(1.0)

        assertThat(t, `is`(1.0))

        val subscene2 = object : SubScene() {
            override val isAllowConcurrency: Boolean = true

            override fun onUpdate(tpf: Double) {
                t += tpf
            }
        }

        window.pushState(subscene)
        window.pushState(subscene2)

        window.onUpdate(1.0)

        assertThat(t, `is`(3.0))
    }
}