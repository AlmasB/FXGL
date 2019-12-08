package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.dsl.getGameTimer
import com.almasb.fxgl.entity.components.BooleanComponent
import javafx.util.Duration

class IntervalSwitchComponent(initValue: Boolean = false,
                              private val interval: Duration = Duration.ZERO): BooleanComponent(initValue) {

    override fun onAdded() {
        onUpdate()
    }

    fun onUpdate() {
        // after a certain interval set valueProperty() to false, then to true and so on.
        value = !value
        getGameTimer().runOnceAfter(Runnable { onUpdate() }, interval)
    }
}