/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.util

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class LazyValueTest {

    @Test
    fun `LazyValue is lazy and instantiated once only`() {
        var count = 0

        val value = LazyValue<String>(Supplier {
            count++
            ""
        })

        assertThat(count, `is`(0))

        assertThat(value.get(), `is`(""))
        assertThat(count, `is`(1))

        assertThat(value.get(), `is`(""))
        assertThat(count, `is`(1))
    }
}