/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.entity.component.Component
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

    private var time = 0.0

    override fun onUpdate(tpf: Double) {
        time += tpf

        if (animate) {
            updateOpacity()
        }

        if (time >= expire.toSeconds()) {
            entity.removeFromWorld()
        }
    }

    private fun updateOpacity() {
        entity.opacity = if (time >= expire.toSeconds()) 0.0 else 1 - time / expire.toSeconds()
    }

    /**
     * Enables diminishing opacity over time.
     *
     * @return this component
     */
    fun animateOpacity() = this.apply {
        animate = true
    }

    override fun isComponentInjectionRequired(): Boolean = false
}