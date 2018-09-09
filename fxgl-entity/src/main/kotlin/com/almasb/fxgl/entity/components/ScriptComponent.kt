/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components

import com.almasb.fxgl.entity.component.Component

/**
 * https://github.com/AlmasB/FXGL/issues/572
 *
 *  TODO: impl integration
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ScriptComponent : Component() {
//
//    private val scripts = ObjectMap<String, Script>()
//
//    fun fireScriptEvent(event: EntityEvent) {
//        getScriptHandler(event.name).ifPresent {
//
//            entity.properties.keys()
//                    .filter { it.startsWith(event.name) }
//                    .forEach { event.setData(it.removePrefix("${event.name}."), event.targetEntity.getPropertyOptional<Any>(it).get()) }
//
//
//            it.call<Void>(event.name, ScriptFactory.newScriptObject(event.data.toMap()
//                    // here we can populate properties common to all events, e.g. entity
//                    .plus("entity" to entity)
//            ))
//        }
//    }
//
//    /**
//     * Searches for a property with key eventName and uses its value
//     * to load a script, which is then cached.
//     * The script typically takes an event object, but
//     * custom scripts are allowed.
//     *
//     * @param eventName e.g. onActivate, onHit, onLevelUp
//     * @return a script that handles a specific event (eventName)
//     */
//    private fun getScriptHandler(eventName: String): Optional<Script> {
//        if (scripts.containsKey(eventName)) {
//            return Optional.of(scripts.get(eventName))
//        }
//
//        // else the script is loaded for the first time
//
//        return entity.getPropertyOptional<Any>(eventName).map({ scriptFile ->
//            val script = FXGL.getAssetLoader().loadScript(scriptFile as String)
//
//            scripts.put(eventName, script)
//
//            script
//        })
//    }
}