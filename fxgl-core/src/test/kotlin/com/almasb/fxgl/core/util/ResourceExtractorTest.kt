/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.util

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author Almas Baim (https://github.com/AlmasB)
 */
class ResourceExtractorTest {

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `File is correctly extracted from resources`() {
        val testFile = Paths.get(System.getProperty("user.home"))
            .resolve(".openjfx")
            .resolve("cache")
            .resolve("fxgl-21")
            .resolve("test_file.txt")

        Files.deleteIfExists(testFile)

        assertTrue(Files.notExists(testFile))

        val file = Paths.get(
            ResourceExtractor.extract(javaClass.getResource("/com/almasb/fxgl/localization/LocalEnglish.properties"), "test_file.txt").toURI()
        )

        val s = Files.readString(file)

        Files.deleteIfExists(file)

        assertThat(s, `is`("data.key = Data2"))
    }
}