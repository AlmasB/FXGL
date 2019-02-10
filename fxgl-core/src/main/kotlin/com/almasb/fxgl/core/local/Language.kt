/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.local

import java.util.*

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Language(val name: String,
               val resourceBundle: ResourceBundle) {

    override fun equals(other: Any?): Boolean {
        if (other !is Language)
            return false

        return other.name == name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return name.toUpperCase()
    }
}