/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.localization

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
class LocalizationService  {

    private val log = Logger.get(javaClass)

    private val languagesData = hashMapOf<Language, HashMap<String, String>>()

    /**
     * Supported languages in alphabetical order.
     */
    val languages: List<Language>
        get() = languagesData.keys.sortedBy { it.name }

    private val selectedLanguageProp = SimpleObjectProperty<Language>()

    fun selectedLanguageProperty() = selectedLanguageProp

    val selectedLanguage: Language
        get() = selectedLanguageProp.value

    fun addLanguageData(lang: Language, bundle: ResourceBundle) {
        val map = languagesData[lang] ?: hashMapOf()
        bundle.keySet().forEach {
            map[it] = bundle.getString(it)
        }
        languagesData[lang] = map
    }

    fun addLanguageData(lang: Language, data: Map<String, String>) {
        val map = languagesData[lang] ?: hashMapOf()
        map.putAll(data)
        languagesData[lang] = map
    }

    /**
     * @return binding to a string translated to given language
     */
    fun localizedStringProperty(key: String): StringBinding {
        return Bindings.createStringBinding(Callable { getLocalizedString(key) }, selectedLanguageProp)
    }

    /**
     * @return a string translated to given language
     */
    fun getLocalizedString(key: String): String {
        return getLocalizedString(key, selectedLanguage)
    }

    /**
     * @return a string translated to given language
     */
    fun getLocalizedString(key: String, lang: Language): String {
        if (lang !in languagesData) {
            log.warning("No data for language $lang")
            return "MISSING_LANG!"
        }

        val data = languagesData[lang]!!

        if (key !in data) {
            log.warning("$key is not localized for language $lang")
            return "MISSING_KEY!"
        }

        return data[key]!!
    }
}