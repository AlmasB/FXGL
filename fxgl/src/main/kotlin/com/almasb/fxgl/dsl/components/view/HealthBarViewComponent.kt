/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components.view

import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.paint.Color

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class HealthBarViewComponent(x: Double, y: Double) : GenericBarViewComponent(x, y, SimpleIntegerProperty(2999), Color.GREEN) {

    init {
        super.maxValue.value = 4003
    }
}