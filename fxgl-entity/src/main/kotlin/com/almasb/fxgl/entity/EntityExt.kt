/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import com.almasb.fxgl.entity.component.Component

/**
 * @return component of given type or throws exception if entity has no such component
 */
inline fun <reified T : Component> Entity.getComponent(): T {
    return this.getComponent(T::class.java)
}
