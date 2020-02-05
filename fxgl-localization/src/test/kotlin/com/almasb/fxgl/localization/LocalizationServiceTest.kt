/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.localization

import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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

        local.addLanguageData(Language.ENGLISH, mapOf(
                "data.key" to "Data2"
        ))

        local.selectedLanguage = Language.ENGLISH

        assertThat(local.selectedLanguage, `is`(Language.ENGLISH))
        assertThat(local.selectedLanguageProperty().value, `is`(Language.ENGLISH))
        assertThat(local.getLocalizedString("data.key"), `is`("Data2"))
    }
}