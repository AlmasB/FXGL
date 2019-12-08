/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.entity.components.BooleanComponent
import com.almasb.fxgl.time.LocalTimer
import javafx.util.Duration

class IntervalSwitchComponent(initValue: Boolean = false,
                              var interval: Duration = Duration.ZERO): BooleanComponent(initValue) {

    private val timer: LocalTimer = FXGL.newLocalTimer()

    fun onUpdate() {
        // after a certain interval set valueProperty() to false, then to true and so on.
        if (timer.elapsed(interval)) {
            value = !value
            timer.capture()
        }
    }
}