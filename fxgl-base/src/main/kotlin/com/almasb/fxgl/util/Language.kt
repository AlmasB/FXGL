/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.util

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
enum class Language {
    ENGLISH, FRENCH, GERMAN, RUSSIAN, HUNGARIAN;

    fun resourceBundleName(): String = this.toString().toLowerCase()
}