/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.component

import com.almasb.fxgl.app.fire
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.EntityEvent

/**
 * When added, the component is set to non-activated.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ActivatorComponent
@JvmOverloads constructor(
        var canBeDeactivated: Boolean = true,
        var numTimesCanBeActivated: Int = Int.MAX_VALUE
): BooleanComponent(false) {

    private var numTimesActivated = 0

    var isActivated: Boolean
        get() = value
        set(v) {
            value = v
        }

    /**
     * Also fires [EntityEvent.ACTIVATE].
     *
     * @param caller - who activated this entity
     */
    fun activate(caller: Entity) {
        if (!isActivated && numTimesActivated < numTimesCanBeActivated) {
            isActivated = true
            numTimesActivated++

            val event = EntityEvent(EntityEvent.ACTIVATE, caller, entity)

            fire(event, "onActivate")

            if (!canBeDeactivated) {
                isActivated = false
            }
        }
    }

    /**
     * Deactivates this component so that it can be activated again.
     */
    fun deactivate(caller: Entity) {
        if (canBeDeactivated && isActivated) {
            isActivated = false
        }
    }
}