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
import com.almasb.fxgl.parser.JavaScriptParser

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

    private var js: JavaScriptParser? = null

    /**
     * If called on an "alive" entity, it will become "dead"
     * and [EntityEvent.DEATH] will be fired.
     */
    fun kill(killer: Entity) {
        if (isAlive()) {
            value = false

            val event = EntityEvent(EntityEvent.DEATH, killer, entity)

            fire(event, "onDeath")

//            entity.properties.keys()
//                    .filter { it.startsWith("onDeath") }
//                    .forEach { event.setData(it.removePrefix("onDeath."), entity.getProperty(it)) }
//
//            fire(event)
//
//
//
//
//            entity.getPropertyOptional<String>("onDeath").ifPresent {
//
//                // TODO: check if the same script file name or else load the new one
//                if (js == null) {
//                    js = JavaScriptParser(it)
//                }
//
//
//
//
//                var script = "function e() { var obj = {}; "
//
//                event.data.forEach {
//                    script += "obj." + it.key + " = " + wrapValue(it.value) + ";"
//                }
//
//                script += "return obj; } e();"
//
//
//
//                js?.let {
//                    it.callFunction<Void>("onDeath", it.eval<Any>(script))
//                }
//            }
        }
    }

    private fun wrapValue(value: Any): String {
        return if (value is String) "\"$value\"" else "$value"
    }

    /**
     * If called on a "dead" entity, it will become "alive"
     * and [EntityEvent.REVIVE] will be fired.
     */
    fun revive() {
        if (isDead()) {
            value = true
            fire(EntityEvent(EntityEvent.REVIVE, entity, entity))
        }
    }

    /**
     * If called on a "dead" entity, it will become "alive"
     * and [EntityEvent.REVIVE] will be fired.
     */
    fun revive(caller: Entity) {
        if (isDead()) {
            value = true
            fire(EntityEvent(EntityEvent.REVIVE, caller, entity))
        }
    }
}