/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.time

import javafx.beans.property.SimpleBooleanProperty
import javafx.util.Duration
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.hasItems
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FPSCounterTest {

    private lateinit var counter: FPSCounter

    @BeforeEach
    fun `setUp`() {
        counter = FPSCounter()
    }

    @Test
    fun `Counters returns 60 fps if JavaFX timing is right`() {
        var count = 1L

        for (i in 1..100) {
            count += 16_666_666
            counter.update(count)
        }

        val fps = counter.update(count + 16_666_666)

        assertThat(fps, `is`(60))
    }
}