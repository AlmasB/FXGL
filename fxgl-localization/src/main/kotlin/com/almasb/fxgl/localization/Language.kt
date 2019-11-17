/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.localization

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Language(name: String) {

    val name = name.toUpperCase()

    companion object {
        @JvmField val ENGLISH = Language("ENGLISH")
        @JvmField val FRENCH = Language("FRENCH")
        @JvmField val GERMAN = Language("GERMAN")
        @JvmField val RUSSIAN = Language("RUSSIAN")
        @JvmField val HUNGARIAN = Language("HUNGARIAN")

        val builtInLanguages = listOf(ENGLISH, FRENCH, GERMAN, RUSSIAN, HUNGARIAN)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Language)
            return false

        return other.name == name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String = name
}