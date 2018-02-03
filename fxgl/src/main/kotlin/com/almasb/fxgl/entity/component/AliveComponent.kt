/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.component

import com.almasb.fxgl.app.fire
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.EntityEvent
import com.almasb.fxgl.parser.JSEvents

/**
 * Adds knowledge about alive / dead state to the entity.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@JSEvents("onDeath", "onRevive")
class AliveComponent
@JvmOverloads constructor(value: Boolean = true) : BooleanComponent(value) {

    fun isAlive() = value
    fun isDead() = !isAlive()

    /**
     * If called on an "alive" entity, it will become "dead"
     * and [EntityEvent.DEATH] will be fired.
     */
    fun kill(killer: Entity) {
        if (isAlive()) {
            value = false

            val event = EntityEvent(EntityEvent.DEATH, killer, entity)

            fire(event, "onDeath")
        }
    }

    /**
     * If called on a "dead" entity, it will become "alive"
     * and [EntityEvent.REVIVE] will be fired.
     */
    fun revive() {
        if (isDead()) {
            value = true

            val event = EntityEvent(EntityEvent.REVIVE, entity, entity)

            fire(event, "onRevive")
        }
    }

    /**
     * If called on a "dead" entity, it will become "alive"
     * and [EntityEvent.REVIVE] will be fired.
     */
    fun revive(caller: Entity) {
        if (isDead()) {
            value = true

            val event = EntityEvent(EntityEvent.REVIVE, caller, entity)

            fire(event, "onRevive")
        }
    }
}