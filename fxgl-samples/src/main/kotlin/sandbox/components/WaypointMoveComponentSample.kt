/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.components

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.GameSettings
import com.almasb.fxgl.dsl.*
import com.almasb.fxgl.dsl.components.WaypointMoveComponent
import javafx.geometry.Point2D
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Polygon

/**
 * Sample showing WaypointMoveComponent with rotation enabled.
 *
 * The arrow-shaped entity moves between several waypoints and rotates
 * towards the next waypoint as it changes direction.
 */
class WaypointMoveComponentSample : GameApplication() {

    override fun initSettings(settings: GameSettings) {
        settings.width = 800
        settings.height = 600
        settings.title = "WaypointMoveComponent Sample"
    }

    override fun initGame() {
        getGameScene().setBackgroundColor(Color.BLACK)

        val waypoints = listOf(
                Point2D(100.0, 100.0),
                Point2D(650.0, 100.0),
                Point2D(650.0, 450.0),
                Point2D(100.0, 450.0),
                Point2D(100.0, 100.0)
        )

        // Draw waypoint markers so the movement path is visible.
        waypoints.forEach { point ->
            entityBuilder()
                    .at(point.x, point.y)
                    .view(Circle(6.0, Color.RED))
                    .buildAndAttach()
        }

        // Arrow shape pointing right, so rotation is clearly visible.
        val arrow = Polygon(
                0.0, 0.0,
                50.0, 20.0,
                0.0, 40.0
        ).apply {
            fill = Color.BLUE
            stroke = Color.WHITE
        }

        entityBuilder()
                .at(100.0, 100.0)
                .view(arrow)
                .with(WaypointMoveComponent(150.0, waypoints).allowRotation(true))
                .buildAndAttach()
    }
}

fun main(args: Array<String>) {
    GameApplication.launch(WaypointMoveComponentSample::class.java, args)
}