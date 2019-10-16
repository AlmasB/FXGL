/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.effects

import com.almasb.fxgl.dsl.components.Effect
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.components.TimeComponent
import javafx.util.Duration

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SlowTimeEffect(
        /**
         * A value in [0..1], 1 means 100%, 0.5 means the tpf is twice as slow (small than normal).
         */
        val ratio: Double,

        duration: Duration) : Effect(duration) {

    override fun onStart(entity: Entity) {
        entity.getComponent(TimeComponent::class.java).value = ratio
    }

    override fun onEnd(entity: Entity) {
        entity.getComponent(TimeComponent::class.java).value = 1.0
    }
}