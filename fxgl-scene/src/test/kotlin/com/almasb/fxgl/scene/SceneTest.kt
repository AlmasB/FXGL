/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene

import com.almasb.fxgl.time.Timer
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.layout.Region
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Scale
import javafx.scene.transform.Transform
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SceneTest {

    @Test
    fun `Listeners fire on scene update`() {
        val scene = object : Scene() {}

        var count = 0.0

        val listener = object : SceneListener {
            override fun onUpdate(tpf: Double) {
                count = tpf
            }
        }

        scene.addListener(listener)

        scene.update(0.016)
        assertThat(count, `is`(0.016))

        scene.removeListener(listener)

        scene.update(1.0)
        assertThat(count, `is`(0.016))
    }

    @Test
    fun `SubScene is always substate`() {
        val scene = object : SubScene() {}
        assertTrue(scene.isSubState)
    }

    @Test
    fun `Default methods are noop`() {
        val sceneService = object : SceneService() {
            override val overlayRoot: Group
                get() = Group()
            override val appWidth: Int
                get() = 600
            override val appHeight: Int
                get() = 600
            override val timer: Timer
                get() = Timer()

            override fun pushSubScene(subScene: SubScene) {
            }

            override fun popSubScene() {
            }
        }
        val scene = object : Scene() {}

        scene.onCreate()
        scene.onDestroy()
        scene.onEnteredFrom(scene)
        scene.onExitingTo(scene)

        assertFalse(scene.isSubState)
        assertFalse(scene.isAllowConcurrency)
    }

    @Test
    fun `Adding and removing children to from scene`() {
        val scene = object : Scene() {}

        val rect = Rectangle()

        assertThat(scene.root.children, contains<Node>(scene.contentRoot))
        assertTrue(scene.contentRoot.children.isEmpty())

        scene.addChild(rect)
        assertFalse(scene.contentRoot.children.isEmpty())
        assertThat(scene.contentRoot.children, contains<Node>(rect))

        scene.removeChild(rect)
        assertTrue(scene.contentRoot.children.isEmpty())
    }

    @Test
    fun `Bind size to given scaled transform`() {
        val scene = object : Scene() {}

        assertThat(scene.root.prefWidth, `is`(Region.USE_COMPUTED_SIZE))
        assertThat(scene.root.prefHeight, `is`(Region.USE_COMPUTED_SIZE))
        assertTrue(scene.contentRoot.transforms.isEmpty())

        val scaledW = SimpleDoubleProperty(400.0)
        val scaledH = SimpleDoubleProperty(200.0)
        val scaleRatioX = SimpleDoubleProperty(4.0)
        val scaleRatioY = SimpleDoubleProperty(2.0)

        scene.bindSize(scaledW, scaledH, scaleRatioX, scaleRatioY)

        assertThat(scene.root.prefWidth, `is`(400.0))
        assertThat(scene.root.prefHeight, `is`(200.0))
        assertThat(scene.contentRoot.transforms.size, `is`(1))

        val scale = scene.contentRoot.transforms[0] as Scale

        assertThat(scale.x, `is`(4.0))
        assertThat(scale.y, `is`(2.0))
    }

    @Test
    fun `toString returns class name`() {
        assertThat(TestScene().toString(), `is`("TestScene"))
    }

    private class TestScene : Scene()
}