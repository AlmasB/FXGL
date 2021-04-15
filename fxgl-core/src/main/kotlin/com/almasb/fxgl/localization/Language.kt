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
        // this language list is a cross-reference adaptation of Android 10 and iOS 13 languages
        // it will grow as the community members add new languages and translations

        @JvmField val NONE = Language("NONE")

        @JvmField val ARABIC = Language("ARABIC")
        @JvmField val CATALAN = Language("CATALAN")
        @JvmField val CHINESE = Language("CHINESE")
        @JvmField val CROATIAN = Language("CROATIAN")
        @JvmField val CZECH = Language("CZECH")
        @JvmField val DANISH = Language("DANISH")
        @JvmField val DUTCH = Language("DUTCH")
        @JvmField val ENGLISH = Language("ENGLISH")
        @JvmField val ESTONIAN = Language("ESTONIAN")
        @JvmField val FILIPINO = Language("FILIPINO")
        @JvmField val FINNISH = Language("FINNISH")
        @JvmField val FRENCH = Language("FRENCH")
        @JvmField val GERMAN = Language("GERMAN")
        @JvmField val GREEK = Language("GREEK")
        @JvmField val HEBREW = Language("HEBREW")
        @JvmField val HINDI = Language("HINDI")
        @JvmField val HUNGARIAN = Language("HUNGARIAN")
        @JvmField val INDONESIAN = Language("INDONESIAN")
        @JvmField val ITALIAN = Language("ITALIAN")
        @JvmField val JAPANESE = Language("JAPANESE")
        @JvmField val KOREAN = Language("KOREAN")
        @JvmField val MALAY = Language("MALAY")
        @JvmField val NORWEGIAN = Language("NORWEGIAN")
        @JvmField val PORTUGUESE = Language("PORTUGUESE")
        @JvmField val ROMANIAN = Language("ROMANIAN")
        @JvmField val RUSSIAN = Language("RUSSIAN")
        @JvmField val SLOVAK = Language("SLOVAK")
        @JvmField val SPANISH = Language("SPANISH")
        @JvmField val SWEDISH = Language("SWEDISH")
        @JvmField val THAI = Language("THAI")
        @JvmField val TURKISH = Language("TURKISH")
        @JvmField val UKRAINIAN = Language("UKRAINIAN")
        @JvmField val VIETNAMESE = Language("VIETNAMESE")
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