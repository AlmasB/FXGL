/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.entity.components.BooleanComponent

/**
 * When added, the component is set to non-activated.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ActivatorComponent
@JvmOverloads constructor(
        var canBeDeactivated: Boolean = true,

        /**
         * This count is only honored when calling [activate] or [deactivate].
         * Setting the value of [isActivated] directly ignores this count.
         */
        var numTimesCanBeActivated: Int = Int.MAX_VALUE
): BooleanComponent(false) {

    private var numTimesActivated = 0

    var isActivated: Boolean
        get() = value
        set(v) { value = v }

    /**
     * If not activated calls [activate], otherwise calls [deactivate].
     */
    fun press() {
        if (isActivated) {
            deactivate()
        } else {
            activate()
        }
    }

    fun activate() {
        if (!isActivated && numTimesActivated < numTimesCanBeActivated) {
            isActivated = true
            numTimesActivated++
        }
    }

    /**
     * Deactivates this component so that it can be activated again.
     */
    fun deactivate() {
        if (canBeDeactivated && isActivated) {
            isActivated = false
        }
    }
}