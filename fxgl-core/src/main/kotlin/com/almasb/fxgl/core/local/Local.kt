/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.local

import com.almasb.sslogger.Logger
import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.beans.property.SimpleObjectProperty
import java.util.*
import java.util.concurrent.Callable

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object Local {

    private val log = Logger.get(javaClass)

    private val langs = arrayListOf<Language>()

    @JvmStatic val languages: List<Language>
        get() = ArrayList(langs)

    private val selectedLanguage = SimpleObjectProperty<Language>()

    @JvmStatic fun selectedLanguageProperty() = selectedLanguage

    @JvmStatic fun addLanguage(name: String, resourceBundle: ResourceBundle) {
        val lang = Language(name, resourceBundle)

        require(lang !in langs) {
            "Language with name \"${lang.name}\" already exists"
        }

        langs += lang
    }

    /**
     * @return a string translated to given language
     */
    @JvmStatic fun getLocalizedString(key: String): String {
        return getLocalizedString(key, selectedLanguage.value)
    }

    /**
     * @return a string translated to given language
     */
    @JvmStatic fun getLocalizedString(key: String, lang: Language): String {
        val bundle = lang.resourceBundle

        try {
            return bundle.getString(key)
        } catch (e: Exception) {
            log.warning("$key is not localized for language $lang")
            return "MISSING_KEY!"
        }
    }

    /**
     * @return binding to a string translated to given language
     */
    @JvmStatic fun localizedStringProperty(key: String): StringBinding {
        return Bindings.createStringBinding(Callable { getLocalizedString(key, selectedLanguage.value) }, selectedLanguage)
    }
}