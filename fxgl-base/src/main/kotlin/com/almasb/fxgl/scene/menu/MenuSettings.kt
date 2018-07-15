/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene.menu

import com.almasb.fxgl.io.serialization.Bundle
import com.almasb.fxgl.saving.UserProfile
import com.almasb.fxgl.saving.UserProfileSavable
import com.almasb.fxgl.util.Language
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class MenuSettings : UserProfileSavable {

    private val language = SimpleObjectProperty<Language>(Language.ENGLISH)

    fun setLanguage(language: Language) {
        this.language.value = language
    }

    fun getLanguage(): Language = language.get()

    fun languageProperty(): ObjectProperty<Language> = language

    private val fullScreen = SimpleBooleanProperty(false)

    fun fullScreenProperty(): BooleanProperty = fullScreen

    override fun save(profile: UserProfile) {
        val bundle = Bundle("menusettings")

        bundle.put("fullscreen", fullScreen.value)

        profile.putBundle(bundle)
    }

    override fun load(profile: UserProfile) {
        val bundle = profile.getBundle("menusettings")
        fullScreen.value = bundle.get("fullscreen")
    }
}