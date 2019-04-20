/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.entity.component.Component

/**
 * A component that dispatches onUpdate by skipping given number of frames.
 * If number of frames to skip is 0 then [onAccumulatedUpdate] is called every frame.
 * If number of frames to skip is 1 then [onAccumulatedUpdate] is called every 2nd frame, etc.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class AccumulatedUpdateComponent(var numFramesToSkip: Int) : Component() {

    private var ac = 0
    private var acTime = 0.0

    override final fun onUpdate(tpf: Double) {
        ac++
        acTime += tpf

        if (ac > numFramesToSkip) {
            onAccumulatedUpdate(acTime)
            ac = 0
            acTime = 0.0
        }
    }

    /**
     * @param tpfSum accumulated tpf since last call of this function
     */
    abstract fun onAccumulatedUpdate(tpfSum: Double)
}