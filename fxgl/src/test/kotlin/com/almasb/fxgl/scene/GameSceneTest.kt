/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene

import com.almasb.fxgl.app.GameScene
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.GameWorld
import com.almasb.fxgl.gameplay.GameState
import com.almasb.fxgl.particle.ParticleComponent
import com.almasb.fxgl.particle.ParticleEmitters
import com.almasb.fxgl.physics.PhysicsWorld
import com.almasb.fxgl.ui.UI
import com.almasb.fxgl.ui.UIController
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.scene.shape.Rectangle
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class GameSceneTest {

    private lateinit var gameScene: GameScene
    private lateinit var world: GameWorld
    private lateinit var state: GameState

    @BeforeEach
    fun setUp() {
        world = GameWorld()
        state = GameState()
        gameScene = GameScene(800, 600, state, world, PhysicsWorld(600, 50.0))
    }

    @Test
    fun `Creation`() {
        assertThat(gameScene.gameWorld, `is`(world))
        assertThat(gameScene.gameState, `is`(state))
    }

    @Test
    fun `Add and remove entity adds its view to game scene and removes`() {
        val gameRoot = gameScene.contentRoot.children[0] as Group

        assertThat(gameRoot.children.size, `is`(0))

        val e = Entity()

        world.addEntity(e)
        assertThat(gameRoot.children.size, `is`(1))

        world.onUpdate(0.016)

        world.removeEntity(e)
        assertThat(gameRoot.children.size, `is`(0))
    }

    @Test
    fun `Add and remove particles to game scene`() {
        val gameRoot = gameScene.contentRoot.children[0] as Group

        assertThat(gameRoot.children.size, `is`(0))

        val e = Entity()
        e.addComponent(ParticleComponent(ParticleEmitters.newFireEmitter()))

        world.addEntity(e)
        assertThat(gameRoot.children.size, `is`(1))

        //world.onUpdate(0.016)

        world.removeEntity(e)

        //world.onUpdate(0.016)

        assertThat(gameRoot.children.size, `is`(0))
    }

    @Test
    fun `Add and remove UI node`() {
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