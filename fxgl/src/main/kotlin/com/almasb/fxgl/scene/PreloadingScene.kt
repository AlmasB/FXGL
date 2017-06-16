/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.util.Duration

/**
 * This is the default preloading scene which is shown while FXGL is being
 * configured and initialized.
 * Hence, this scene is purely JavaFX based.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class PreloadingScene : Scene(VBox(50.0)) {

    // seconds
    private val WAIT_TIME = 10

    init {
        createContent()
    }

    private fun createContent(): Parent {
        val root = this.root as VBox

        with(root) {
            setPrefSize(400.0, 400.0)
            background = Background(BackgroundFill(Color.BLACK, null, null))
            padding = Insets(25.0)
            alignment = Pos.CENTER
            children.addAll(makeTitle(), makeSymbol())

            return this
        }
    }

    private fun makeTitle(): Node {
        val text = Text("FXGL")

        with(text) {
            fill = Color.WHITE
            font = Font.font(48.0)
            return this
        }
    }

    private fun makeSymbol(): Node {
        val symbol = Pane()
        symbol.translateX = 125.0

        val top = Rectangle(70.0, 5.0, Color.BLUE)
        top.arcWidth = 25.0
        top.arcHeight = 25.0

        val mid = Rectangle(100.0, 5.0, Color.BLUE)
        mid.arcWidth = 25.0
        mid.arcHeight = 25.0

        val bot = Rectangle(70.0, 5.0, Color.BLUE)
        bot.arcWidth = 25.0
        bot.arcHeight = 25.0

        top.translateX = 15.0
        bot.translateX = 15.0

        top.translateY = 10.0
        mid.translateY = 10 + 10 + 5.0
        bot.translateY = 10 + 10 + 5 + 10 + 5.0

        val outerCircle = Circle(25.0, 25.0, 25.0, Color.BLACK)
        outerCircle.stroke = Color.BLUE
        outerCircle.strokeWidth = 3.0
        outerCircle.translateX = 25.0

        val innerCircle = Circle(25.0, 25.0, 25.0, Color.BLACK)
        innerCircle.stroke = Color.BLUE
        innerCircle.strokeWidth = 1.5
        innerCircle.translateX = 25.0
        innerCircle.radius = 2.0

        val point = Circle(25.0, 25.0, 25.0, Color.GREEN)
        point.stroke = Color.GREEN
        point.strokeWidth = 1.5
        point.translateX = 25.0
        point.radius = 1.0

        val frame = KeyFrame(Duration.seconds(1.0),
                KeyValue(innerCircle.radiusProperty(), 20),
                KeyValue(innerCircle.fillProperty(), Color.GREEN))

        val timeline = Timeline()
        with(timeline) {
            keyFrames.add(frame)
            cycleCount = WAIT_TIME
            setOnFinished {
                showExit()
            }
            play()
        }

        symbol.children.addAll(top, mid, bot, outerCircle, innerCircle, point)
        return symbol
    }

    private fun showExit() {
        val text = Text("Taking longer than usual...")
        text.fill = Color.WHITE

        val exitBtn = Button("EXIT")
        exitBtn.setOnAction {
            println("User requested exit")
            System.exit(0)
        }

        (root as VBox).children.addAll(text, exitBtn)
    }
}