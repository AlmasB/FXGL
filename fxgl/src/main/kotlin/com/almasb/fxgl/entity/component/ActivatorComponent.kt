/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.component

import com.almasb.fxgl.app.fire
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.EntityEvent
import com.almasb.fxgl.parser.JavaScriptParser

/**
 * TODO: can be activated? how many times? deactivate?
 *
 * When added the component is set to non-activated
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ActivatorComponent : BooleanComponent(false) {

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
        isActivated = true
        fire(EntityEvent(EntityEvent.ACTIVATE, caller, entity))

//        entity.getPropertyOptional<String>("onActivate").ifPresent {
//            JavaScriptParser(it).callFunction("onActivate", entity, caller)
//        }
    }
}