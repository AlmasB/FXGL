/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.localization

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.logging.Logger
import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.beans.property.SimpleObjectProperty
import java.util.*
import java.util.concurrent.Callable

/**
 * Services that provides functions to localize Strings to the given [Language] using
 * String or StringBinding.
 * Also supports lazy loading of language data.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class LocalizationService : EngineService() {

    private val log = Logger.get(javaClass)

    private val languagesDataSuppliers = hashMapOf<Language, () -> Map<String, String>>()
    private val languagesData = hashMapOf<Language, HashMap<String, String>>()

    private val selectedLanguageProp = SimpleObjectProperty(Language.NONE)

    fun selectedLanguageProperty() = selectedLanguageProp

    var selectedLanguage: Language
        get() = selectedLanguageProp.value
        set(value) { selectedLanguageProp.value = value }

    /**
     * Populate service dictionary for [lang] using key-value pairs from [bundle].
     */
    fun addLanguageData(lang: Language, bundle: ResourceBundle) {
        val map = languagesData.getOrDefault(lang, hashMapOf())
        bundle.keySet().forEach {
            map[it] = bundle.getString(it)
        }
        languagesData[lang] = map
    }

    /**
     * Populate service dictionary for [lang] using key-value pairs from [dataSupplier].
     * The [dataSupplier] is invoked lazily when data for [lang] is first requested.
     */
    fun addLanguageDataLazy(lang: Language, dataSupplier: () -> Map<String, String>) {
        languagesDataSuppliers[lang] = dataSupplier
    }

    /**
     * Populate service dictionary for [lang] using key-value pairs from [data].
     */
    fun addLanguageData(lang: Language, data: Map<String, String>) {
        val map = languagesData.getOrDefault(lang, hashMapOf())
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
     * @return a string translated to the selected language, i.e. [selectedLanguage]
     */
    fun getLocalizedString(key: String): String {
        return getLocalizedString(key, selectedLanguage)
    }

    /**
     * @return a string translated to given language [lang]
     */
    fun getLocalizedString(key: String, lang: Language): String {
        if (lang == Language.NONE) {
            return "Language is NONE"
        }

        if (lang !in languagesData) {
            if (lang in languagesDataSuppliers) {
                addLanguageData(lang, languagesDataSuppliers[lang]!!.invoke())
            } else {
                log.warning("No data for language $lang")
                return "MISSING_LANG!"
            }
        }

        val data = languagesData[lang]!!

        if (key !in data) {
            log.warning("$key is not localized for language $lang")
            return "MISSING_KEY!"
        }

        return data[key]!!
    }
}