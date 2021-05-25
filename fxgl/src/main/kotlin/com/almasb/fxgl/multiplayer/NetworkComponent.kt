/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.multiplayer

import com.almasb.fxgl.entity.component.Component

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class NetworkComponent : Component() {

    companion object {
        private var uniqueID = 0L
    }

    var id: Long = uniqueID++
        internal set

    override fun isComponentInjectionRequired(): Boolean = false
}