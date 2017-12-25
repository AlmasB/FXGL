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
 * TODO: can be activated? how many times? deactivate?
 *
 * When added the component is set to non-activated
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@JSEvents("onActivate", "onDeactivate")
class ActivatorComponent : BooleanComponent(false) {

    var isActivated: Boolean
        get() = value
        set(v) {
            value = v
        }

    private var js: JavaScriptParser? = null

    /**
     * Also fires [EntityEvent.ACTIVATE].
     *
     * @param caller - who activated this entity
     */
    fun activate(caller: Entity) {
        isActivated = true
//
        val event = EntityEvent(EntityEvent.ACTIVATE, caller, entity)

        fire(event, "onActivate")
//
//        entity.properties.keys()
//                .filter { it.startsWith("onActivate") }
//                .forEach { event.setData(it.removePrefix("onActivate."), entity.getProperty(it)) }
//
//        fire(event)
//
//
//
//
//        entity.getPropertyOptional<String>("onActivate").ifPresent {
//
//            // TODO: check if the same script file name or else load the new one
//            if (js == null) {
//                js = JavaScriptParser(it)
//            }
//
//
//
//
//            var script = "function e() { var obj = {}; "
//
//            event.data.forEach {
//                script += "obj." + it.key + " = " + wrapValue(it.value) + ";"
//            }
//
//            script += "return obj; } e();"
//
//
//
//            js?.let {
//                it.callFunction<Void>("onActivate", it.eval<Any>(script))
//            }
//        }
    }


}