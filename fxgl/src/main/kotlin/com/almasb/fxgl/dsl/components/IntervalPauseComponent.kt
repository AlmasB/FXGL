/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.dsl.getGameTimer
import com.almasb.fxgl.entity.component.Component
import javafx.util.Duration

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class IntervalPauseComponent(private val map: Map<Class<out Component>, Duration>) : Component() {

    override fun onAdded() {
        map.forEach { (type, interval) ->
            getGameTimer().runAtInterval(Runnable {
                entity.getComponentOptional(type).ifPresent {
                    if (it.isPaused) {
                        it.resume()
                    } else {
                        it.pause()
                    }
                }
            }, interval)
        }
    }
}