/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.serialization

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class BundleTest {

    @Test
    fun `Test access`() {
        val bundle = Bundle("Test")

        val s: String? = bundle.get<String>("None")

        assertTrue(s == null)
    }

    @Test
    fun `exists`() {
        val bundle = Bundle("Test")

        assertFalse(bundle.exists("key"))

        bundle.put("key", "someValue")

        assertTrue(bundle.exists("key"))
    }

    @Test
    fun `To String`() {
        val bundle = Bundle("Test")

        bundle.put("key", "someValue")
        bundle.put("key2", 33)

        assertThat(bundle.toString(), `is`("Bundle Test: {Test.key2=33, Test.key=someValue}"))
    }
}