/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.time.TimerAction
import javafx.util.Duration

/**
 * Removes an entity from the world after a certain duration.
 * Useful for special effects or temporary entities.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class ExpireCleanComponent(

        /**
         * The expire duration timer starts when the entity is attached to the world,
         * so it does not start immediately when this component is created.
         *
         * @param expire the duration after entity is removed from the world
         */
        private val expire: Duration) : Component() {

    private var animate = false

    private var timerAction: TimerAction? = null

    override fun onAdded() {
        entity.activeProperty().addListener { _, _, isActive ->
            if (isActive) {
                timerAction = FXGL.getGameTimer().runOnceAfter({ entity.removeFromWorld() }, expire)
            } else {
                timerAction?.expire()
            }
        }
    }

    override fun onUpdate(tpf: Double) {
        if (timerAction == null) {
            timerAction = FXGL.getGameTimer().runOnceAfter({ entity.removeFromWorld() }, expire)
        } else {

            if (animate) {
                updateOpacity(tpf)
            }
        }
    }

    private var time = 0.0

    private fun updateOpacity(tpf: Double) {
        time += tpf

        getEntity().viewComponent.opacityProp.value = if (time >= expire.toSeconds()) 0.0 else 1 - time / expire.toSeconds()
    }

    /**
     * Enables diminishing opacity over time.
     *
     * @return this component
     */
    fun animateOpacity() = this.apply {
        animate = true
    }
}