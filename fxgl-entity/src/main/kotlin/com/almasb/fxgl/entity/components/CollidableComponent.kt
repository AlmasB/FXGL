/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components

import java.io.Serializable
import java.util.*

/**
 * Marks an entity as collidable.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class CollidableComponent(collidable: Boolean) : BooleanComponent(collidable) {

    private val ignoredTypes = ArrayList<Serializable>()

    fun getIgnoredTypes(): List<Serializable> {
        return ignoredTypes
    }

    fun addIgnoredType(type: Serializable) {
        ignoredTypes.add(type)
    }

    fun removeIgnoredType(type: Serializable) {
        ignoredTypes.remove(type)
    }
}