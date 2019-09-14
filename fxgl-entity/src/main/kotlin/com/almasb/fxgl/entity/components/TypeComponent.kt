/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components

import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.entity.component.CoreComponent
import com.almasb.fxgl.entity.component.SerializableComponent
import java.io.Serializable

/**
 * Represents an entity type.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@CoreComponent
class TypeComponent

/**
 * Constructs a component with given type.
 * Note: although the type could be any object, it is recommended
 * that an enum is used to represent types.
 *
 * @param type entity type
 */
@JvmOverloads constructor(type: Serializable = SObject()) : ObjectComponent<Serializable>(type), SerializableComponent {

    /**
     * @param type entity type
     * @return true iff this type component is of given type
     */
    fun isType(type: Any) = value == type

    override fun toString() = "Type($value)"

    override fun write(bundle: Bundle) {
        bundle.put("value", value)
    }

    override fun read(bundle: Bundle) {
        value = bundle.get("value")
    }

    private class SObject : Serializable {
        override fun toString() = "NONE"

        companion object {
            private const val serialVersionUID = -1L
        }
    }
}