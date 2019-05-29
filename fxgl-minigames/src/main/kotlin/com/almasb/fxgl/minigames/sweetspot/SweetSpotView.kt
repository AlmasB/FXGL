/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.minigames.sweetspot

import com.almasb.fxgl.minigames.MiniGameView
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SweetSpotView : MiniGameView<SweetSpotMiniGame>(SweetSpotMiniGame()) {

    init {
        val initWidth = 200.0
        val initHeight = 50.0

        val bg = Rectangle(initWidth, initHeight, null)
        bg.arcWidth = 15.0
        bg.arcHeight = 15.0
        bg.stroke = Color.BLACK
        bg.strokeWidth = 2.5

        miniGame.minSuccessValue.value = 30
        miniGame.maxSuccessValue.value = 55



        val rect1 = Rectangle(0.0, bg.height)
        rect1.widthProperty().bind(bg.widthProperty().multiply(miniGame.minSuccessValue).divide(100.0))
        rect1.translateX = 1.5
        rect1.fill = Color.RED

        val rect2 = Rectangle(0.0, bg.height)
        rect2.widthProperty().bind(bg.widthProperty().multiply(miniGame.maxSuccessValue.subtract(miniGame.minSuccessValue)).divide(100))
        rect2.translateXProperty().bind(bg.widthProperty().multiply(miniGame.minSuccessValue).divide(100))
        rect2.fill = Color.GREEN.brighter().brighter()

        val rect3 = Rectangle(0.0, bg.height)
        rect3.widthProperty().bind(bg.widthProperty().subtract(1.5).multiply(SimpleIntegerProperty(100).subtract(miniGame.maxSuccessValue)).divide(100))
        rect3.translateXProperty().bind(bg.widthProperty().multiply(miniGame.maxSuccessValue).divide(100))
        rect3.fill = Color.RED



        val cursor = Rectangle(4.0, 14.0, Color.DARKBLUE)
        cursor.translateY = bg.height - 5.0
        cursor.translateXProperty().bind(bg.widthProperty().multiply(miniGame.cursorValue).divide(100))

        children.addAll(rect1, rect2, rect3, bg, cursor)

        setOnMouseClicked {
            miniGame.click()
        }
    }
}