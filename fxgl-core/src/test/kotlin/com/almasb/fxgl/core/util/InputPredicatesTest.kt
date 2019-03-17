/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.util

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class InputPredicatesTest {

    @Test
    fun `Alphanum`() {
        InputPredicates()

        val s1 = "abassd"
        val s2 = "2asadsda"
        val s3 = "%2asdasd"
        val s4 = "asda sad"

        assertTrue(InputPredicates.ALPHANUM.test(s1))
        assertTrue(InputPredicates.ALPHANUM.test(s2))
        assertFalse(InputPredicates.ALPHANUM.test(s3))
        assertFalse(InputPredicates.ALPHANUM.test(s4))
    }
}