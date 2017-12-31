/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.control

import com.almasb.fxgl.entity.Control
import com.almasb.fxgl.entity.Entity

/**
 * A control that skips given number of frames.
 * If number of frames to skip is 0 then [onAccumulatedUpdate] is called every frame.
 * If number of frames to skip is 1 then [onAccumulatedUpdate] is called every 2nd frame, etc.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class AccumulatedUpdateControl(var numFramesToSkip: Int) : Control() {

    private var ac = 0
    private var acTime = 0.0

    override final fun onUpdate(entity: Entity, tpf: Double) {
        ac++
        acTime += tpf

        if (ac > numFramesToSkip) {
            onAccumulatedUpdate(entity, acTime)
            ac = 0
            acTime = 0.0
        }
    }

    /**
     * @param tpfSum accumulated tpf since last call of this function
     */
    abstract fun onAccumulatedUpdate(entity: Entity, tpfSum: Double)
}