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
class HealthBarViewComponent(x: Double, y: Double) : ChildViewComponent(x, y, false) {

    val hpBar = ProgressBar()

    init {
        val hp = SimpleIntegerProperty(2999)
        val maxHP = 4003

        hpBar.setMinValue(0.0)
        hpBar.setMaxValue(100.0)
        hpBar.setWidth(100.0)
        hpBar.setLabelVisible(false)
        hpBar.setLabelPosition(Position.RIGHT)
        hpBar.setFill(Color.GREEN)

        hpBar.currentValueProperty().bind(
                hp.divide(maxHP*1.0).multiply(100)
        )

        viewRoot.children.addAll(hpBar)
    }
}