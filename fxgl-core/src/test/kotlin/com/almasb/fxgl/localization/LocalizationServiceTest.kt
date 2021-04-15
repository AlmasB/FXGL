/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.localization

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class LocalizationServiceTest {

    private lateinit var local: LocalizationService

    @BeforeEach
    fun setUp() {
        local = LocalizationService()
    }

    @Test
    fun `Language test`() {
        val language = Language("MyNewLanguage")

        assertThat(language.name, `is`("MYNEWLANGUAGE"))
        assertThat(language.toString(), `is`("MYNEWLANGUAGE"))
        assertThat(language.hashCode(), `is`("MYNEWLANGUAGE".hashCode()))

        assertFalse(language.equals("MYNEWLANGUAGE"))
        assertThat(language, `is`(Language("MYNEWLANGUAGE")))
    }

    @Test
    fun `Localized string does not fail if lang or key not found`() {
        assertDoesNotThrow {
            local.getLocalizedString("bla-bla")

            local.getLocalizedString("bla-bla", Language.ENGLISH)

            local.addLanguageData(Language.ENGLISH, mapOf(
                    "data.key" to "This is data"
            ))

            local.getLocalizedString("bla-bla", Language.ENGLISH)
        }
    }

    @Test
    fun `Keys are correctly translated`() {
        local.addLanguageData(Language.ENGLISH, mapOf(
                "data.key" to "This is data"
        ))

        val result = local.getLocalizedString("data.key", Language.ENGLISH)
        assertThat(result, `is`("This is data"))

        local.addLanguageData(Language.ENGLISH, PropertyResourceBundle(javaClass.getResourceAsStream("LocalEnglish.properties")))

        local.selectedLanguage = Language.ENGLISH

        assertThat(local.selectedLanguage, `is`(Language.ENGLISH))
        assertThat(local.selectedLanguageProperty().value, `is`(Language.ENGLISH))
        assertThat(local.getLocalizedString("data.key"), `is`("Data2"))
    }

    @Test
    fun `Language bindings are updated automatically`() {
        local.addLanguageData(Language.ENGLISH, mapOf(
                "data.key" to "This is data"
        ))

        local.addLanguageData(Language.GERMAN, mapOf(
                "data.key" to "This is data in German"
        ))

        local.selectedLanguage = Language.ENGLISH
        val binding = local.localizedStringProperty("data.key")

        assertThat(binding.value, `is`("This is data"))

        local.selectedLanguage = Language.GERMAN
        assertThat(binding.value, `is`("This is data in German"))
    }

    @Test
    fun `Lazy localization`() {
        local.addLanguageDataLazy(Language.ENGLISH) {
            mapOf(
                "data.key" to "This is data"
            )
        }

        val result = local.getLocalizedString("data.key", Language.ENGLISH)
        assertThat(result, `is`("This is data"))
    }
}