/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene.menu

import com.almasb.fxgl.util.Language
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class MenuSettings {

    private val language = SimpleObjectProperty<Language>(Language.ENGLISH)

    fun setLanguage(language: Language) {
        this.language.value = language
    }

    fun getLanguage(): Language = language.get()

    fun languageProperty(): ObjectProperty<Language> = language
}