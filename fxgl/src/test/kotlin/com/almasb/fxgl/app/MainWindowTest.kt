/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.app

import com.almasb.fxgl.app.scene.FXGLScene
import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.scene.CSS
import com.almasb.fxgl.scene.Scene
import com.almasb.fxgl.scene.SubScene
import com.almasb.fxgl.test.RunWithFX
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
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

        private lateinit var window: PrimaryStageWindow
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

                window = PrimaryStageWindow(stage, scene, settings.toReadOnly())
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
            `Fire JavaFX event`()
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

    /**
     * Integration test for MainWindow-JavaFX scene-FXGLScene-Input event interactions.
     */
    fun `Fire JavaFX event`() {
        var count = 0

        // test a single handler
        val handler = EventHandler<Event> { count++ }

        scene.input.addEventHandler(EventType.ROOT, handler)

        window.stage.scene.root.fireEvent(Event(EventType.ROOT))

        assertThat(count, `is`(1))

        // remove the handler
        scene.input.removeEventHandler(EventType.ROOT, handler)

        window.stage.scene.root.fireEvent(Event(EventType.ROOT))

        assertThat(count, `is`(1))

        // test filters
        val filter = EventHandler<Event> { count -= 3 }

        scene.input.addEventFilter(EventType.ROOT, filter)
        scene.input.addEventHandler(EventType.ROOT, handler)

        window.stage.scene.root.fireEvent(Event(EventType.ROOT))

        // count -= 3 and then count++
        assertThat(count, `is`(-1))

        // now add a consuming filter
        scene.input.removeEventFilter(EventType.ROOT, filter)

        val filterConsume = EventHandler<Event> {
            count -= 5
            it.consume()

            println(it.isConsumed)
        }

        scene.input.addEventFilter(EventType.ROOT, filterConsume)

        window.stage.scene.root.fireEvent(Event(EventType.ROOT))

        // count -= 5 and then consume, event never reaches handler
        assertThat(count, `is`(-6))
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

        window.update(1.0)

        assertThat(t, `is`(1.0))

        window.popState()

        assertThat(window.currentScene, `is`(not<Scene>(subscene)))

        window.update(1.0)

        assertThat(t, `is`(1.0))

        val subscene2 = object : SubScene() {
            override val isAllowConcurrency: Boolean = true

            override fun onUpdate(tpf: Double) {
                t += tpf
            }
        }

        window.pushState(subscene)
        window.pushState(subscene2)

        window.update(1.0)

        assertThat(t, `is`(3.0))
    }
}