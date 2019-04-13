/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components

import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.component.SerializableComponent

/**
 * Adds ID to an entity, so it can be uniquely identified.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class IDComponent
/**
 * Constructs ID component with given entity name and id.
 * The combination of name and id must be unique.
 *
 * @param name string representation of entity name
 * @param id numeric id that uniquely identifies the entity with given name
 */
(name: String, id: Int) : Component(), SerializableComponent {

    var name: String = name
        private set

    var id: Int = id
        private set

    /**
     * @return full id, this must be unique
     */
    val fullID: String
        get() = "$name:$id"

    override fun hashCode(): Int {
        return fullID.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        // just assume it's IDComponent
        return (other as IDComponent).fullID == fullID
    }

    override fun toString(): String {
        return fullID
    }

    override fun write(bundle: Bundle) {
        bundle.put("name", name)
        bundle.put("id", id)
    }

    override fun read(bundle: Bundle) {
        name = bundle.get("name")
        id = bundle.get("id")
    }
}