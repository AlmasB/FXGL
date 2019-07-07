/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components.view

import com.almasb.fxgl.ui.Position
import com.almasb.fxgl.ui.ProgressBar
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.paint.Color

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class HealthBarChildViewComponent(x: Double, y: Double) : ChildViewComponent(x, y) {

    val hpBar = ProgressBar()

    init {
        val hp = SimpleIntegerProperty(44)
        val maxHP = 4003

        hpBar.setMinValue(0.0)
        hpBar.setMaxValue(100.0)
        hpBar.setWidth(300.0)
        hpBar.setLabelVisible(true)
        hpBar.setLabelPosition(Position.RIGHT)
        hpBar.setFill(Color.GREEN)

        hpBar.currentValueProperty().bind(
                hp.divide(maxHP*1.0).multiply(100)
        )

        // TODO: API should be something like
        // children.addAll(hpBar)


        // ViewCOmponent should have two roots, one Group and one Group for dev stuff
    }
}