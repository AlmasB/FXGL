/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.GameSettings
import com.almasb.fxgl.app.scene.GameView
import com.almasb.fxgl.dsl.*
import com.almasb.fxgl.dsl.FXGL.Companion.getAppHeight
import com.almasb.fxgl.dsl.FXGL.Companion.getAppWidth
import com.almasb.fxgl.pathfinding.CellState
import com.almasb.fxgl.pathfinding.astar.AStarGrid
import com.almasb.fxgl.pathfinding.astar.AStarGridView
import javafx.scene.input.MouseButton
import javafx.scene.shape.Rectangle
import java.util.function.Predicate

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ZephTest : GameApplication() {
    override fun initSettings(settings: GameSettings) {
        settings.width = 1700
        settings.height = 800
        settings.isClickFeedbackEnabled = true
        settings.isManualResizeEnabled = true
        settings.isPreserveResizeRatio = true
    }

    override fun initInput() {
        onBtnDown(MouseButton.PRIMARY) {
            val cellX = (FXGL.getInput().mouseXWorld / 32.0).toInt()
            val cellY = (FXGL.getInput().mouseYWorld / 32.0).toInt()

            println("" + getInput().mousePositionWorld + ": $cellX,$cellY")
        }

        onBtnDown(MouseButton.SECONDARY) {
            println(getGameScene().viewport.visibleArea)
        }
    }

    override fun initGame() {
        getGameWorld().addEntityFactory(ZephFactory())

        FXGL.setLevelFromMap(
                "tmx/zeph/test_map.tmx"
        )

        val grid = AStarGrid.fromWorld(getGameWorld(), 150, 150, 32, 32) { type: Any ->
            CellState.WALKABLE
        }

        val view = GameView(AStarGridView(grid, 32, 32), 0)

        getGameScene().addGameView(view)

        val e = entityBuilder()
                .viewWithBBox(Rectangle(32.0, 32.0))
                .with(DeveloperWASDControl())
                .buildAndAttach()

        e.type = "test"

        getGameScene().viewport.setBounds(0*32, 0*32, 150*32, 150*32)
        //getGameScene().viewport.bindToEntity(e, getAppWidth() / 2.0, getAppHeight() / 2.0)

        getGameScene().viewport.setZoom(2.0)
    }

    override fun onUpdate(tpf: Double) {
        getGameWorld().getEntitiesFiltered(Predicate { it.isType("test") }).forEach {
            if (it.isWithin(getGameScene().viewport.visibleArea)) {
                println("true")
            } else {
                println("false")
            }
        }
    }
}

fun main(args: Array<out String>) {
    GameApplication.launch(ZephTest::class.java, args)
}