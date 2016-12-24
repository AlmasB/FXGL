/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
        timeline.getKeyFrames().add(frame)

        // this will run 5 seconds, we assume FXGL is ready by then
        timeline.setCycleCount(5)
        timeline.play()

        symbol.children.addAll(top, mid, bot, outerCircle, innerCircle, point)
        return symbol
    }
}