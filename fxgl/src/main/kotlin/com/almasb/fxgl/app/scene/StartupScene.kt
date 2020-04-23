/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.scene

import com.almasb.fxgl.dsl.getAppHeight
import com.almasb.fxgl.dsl.getAppWidth
import com.almasb.fxgl.dsl.getSettings
import com.almasb.fxgl.logging.Logger
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle

/**
 * This is the default startup scene which is shown while FXGL is in startup state.
 *
 * As soon as the main loop starts, this scene will be switched instantly, so
 * there is no need to do animation in this state.
 *
 * Since we are trying to open a window as soon as possible, in this scene there are
 * no services that are ready.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class StartupScene : FXGLScene() {

    private val log = Logger.get(javaClass)

    override fun onUpdate(tpf: Double) {
        log.debug("STARTUP")

        // Start -> (Intro) -> (Menu) -> Game
        if (getSettings().isIntroEnabled) {
            controller.gotoIntro()
        } else {
            if (getSettings().isMainMenuEnabled) {
                controller.gotoMainMenu()
            } else {
                controller.startNewGame()
            }
        }
    }
}

class FXGLStartupScene : StartupScene() {
    init {
        val bg = Rectangle(getAppWidth().toDouble(), getAppHeight().toDouble())

//        val logo = texture("fxgl_logo.png", 877 * 0.25, 213 * 0.25)
//        logo.translateX = getAppWidth() / 2 - logo.width / 2
//        logo.translateY = getAppHeight() / 2 - logo.height * 2

        val symbol = makeSymbol()
        symbol.translateX = getAppWidth() / 2 - 53.0
        symbol.translateY = getAppHeight() / 2 + 45.0

        contentRoot.children.addAll(bg, symbol)
    }

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

        symbol.children.addAll(top, mid, bot, outerCircle, innerCircle, point)
        return symbol
    }
}