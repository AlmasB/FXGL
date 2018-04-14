/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.centerText
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.util.Duration

/**
 * This is the default startup scene which is shown while FXGL is in startup state.
 *
 * TODO: properly clean this up once done using startup
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class StartupScene : FXGLScene() {

    init {
        val bg = Rectangle(FXGL.getAppWidth().toDouble(), FXGL.getAppHeight().toDouble())

        val title = makeTitle()
        centerText(title)

        val symbol = makeSymbol()
        symbol.translateX = FXGL.getAppWidth() / 2 - 53.0
        symbol.translateY = FXGL.getAppHeight() / 2 + 45.0

        contentRoot.children.addAll(bg, title, symbol)
    }

    private fun makeTitle() = FXGL.getUIFactory().newText("FXGL", 48.0)

    private fun makeSymbol(): Node {
        val symbol = Pane()

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

        // TODO: use FXGL loop?

        val timeline = Timeline()
        timeline.cycleCount = 10
        timeline.keyFrames.add(frame)
        timeline.play()

        symbol.children.addAll(top, mid, bot, outerCircle, innerCircle, point)
        return symbol
    }
}