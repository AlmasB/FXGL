/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

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
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.assertFalse
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

        // add remove multiple

        val rect2 = Rectangle()

        gameScene.addUINodes(rect, rect2)

        assertThat(gameScene.uiNodes, contains<Node>(rect, rect2))

        gameScene.removeUINodes(rect, rect2)

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

    @Test
    fun `Z index is correctly sorted`() {
        val gameRoot = gameScene.contentRoot.children[0] as Group

        assertThat(gameRoot.children.size, `is`(0))

        val view1 = GameView(Rectangle(), 1000)
        gameScene.addGameView(view1)
        assertThat(gameRoot.children[0], `is`(view1.node))

        val view2 = GameView(Rectangle(), 300)
        gameScene.addGameView(view2)
        assertThat(gameRoot.children[0], `is`(view1.node))
        assertThat(gameRoot.children[1], `is`(view2.node))

        // update should sort z indices
        gameScene.update(0.016)
        assertThat(gameRoot.children[0], `is`(view2.node))
        assertThat(gameRoot.children[1], `is`(view1.node))
    }

    @Test
    fun `Clear game views removes all views`() {
        val gameRoot = gameScene.contentRoot.children[0] as Group

        assertThat(gameRoot.children.size, `is`(0))

        val view1 = GameView(Rectangle(), 1000)
        gameScene.addGameView(view1)

        val view2 = GameView(Rectangle(), 300)
        gameScene.addGameView(view2)

        assertThat(gameRoot.children.size, `is`(2))

        gameScene.clearGameViews()

        assertThat(gameRoot.children.size, `is`(0))
    }

    @Test
    fun `Clear removes all game and ui views`() {
        val gameRoot = gameScene.contentRoot.children[0] as Group

        val view1 = GameView(Rectangle(), 1000)
        gameScene.addGameView(view1)

        val view2 = GameView(Rectangle(), 300)
        gameScene.addGameView(view2)

        val rect = Rectangle()

        gameScene.addUINode(rect)

        gameScene.clear()

        assertThat(gameRoot.children.size, `is`(0))
        assertThat(gameScene.uiNodes.size, `is`(0))
    }

    @Test
    fun `Set UI mouse transparent`() {
        val uiRoot = gameScene.contentRoot.children[1] as Group

        assertFalse(uiRoot.isMouseTransparent)

        gameScene.setUIMouseTransparent(true)

        assertTrue(uiRoot.isMouseTransparent)
    }

    @Test
    fun `Single step does not trigger step update`() {
        assertFalse(gameScene.isSingleStep)

        val gameRoot = gameScene.contentRoot.children[0] as Group

        assertThat(gameRoot.children.size, `is`(0))

        val view1 = GameView(Rectangle(), 1000)
        gameScene.addGameView(view1)
        assertThat(gameRoot.children[0], `is`(view1.node))

        val view2 = GameView(Rectangle(), 300)
        gameScene.addGameView(view2)
        assertThat(gameRoot.children[0], `is`(view1.node))
        assertThat(gameRoot.children[1], `is`(view2.node))

        gameScene.isSingleStep = true

        // now update should not trigger frame update, in which case
        // z is not sorted and the order remains the same
        gameScene.update(0.016)

        assertThat(gameRoot.children[0], `is`(view1.node))
        assertThat(gameRoot.children[1], `is`(view2.node))
    }
}