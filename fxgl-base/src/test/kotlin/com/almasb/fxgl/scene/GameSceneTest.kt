/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.FXGLMock
import com.almasb.fxgl.ui.UI
import com.almasb.fxgl.ui.UIController
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.scene.shape.Rectangle
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class GameSceneTest {

    private lateinit var gameScene: GameScene

    @BeforeEach
    fun setUp() {
        gameScene = GameScene(800, 600)
    }

    @Test
    fun `Add and remove UI node `() {
        val rect = Rectangle()

        gameScene.addUINode(rect)

        assertThat(gameScene.uiNodes, containsInAnyOrder<Node>(rect))

        gameScene.removeUINode(rect)

        assertTrue(gameScene.uiNodes.isEmpty())

        gameScene.addUINode(rect)

        assertThat(gameScene.uiNodes, containsInAnyOrder<Node>(rect))

        gameScene.clearUINodes()

        assertTrue(gameScene.uiNodes.isEmpty())
    }

    @Test
    fun `Add and remove UI`() {
        val pane = Pane()
        val controller = object : UIController {
            override fun init() {
            }
        }

        val ui = UI(pane, controller)

        gameScene.addUI(ui)

        assertThat(gameScene.uiNodes, containsInAnyOrder<Node>(pane))

        gameScene.removeUI(ui)

        assertTrue(gameScene.uiNodes.isEmpty())
    }
}