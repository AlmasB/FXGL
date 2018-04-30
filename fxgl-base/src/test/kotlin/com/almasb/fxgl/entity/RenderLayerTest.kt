/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import com.almasb.fxgl.util.Consumer
import com.almasb.fxgl.util.Predicate
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RenderLayerTest {

    @Test
    fun `Predefined layers`() {
        assertThat(RenderLayer.TOP.name(), `is`("TOP"))
        assertThat(RenderLayer.BOTTOM.name(), `is`("BOTTOM"))
        assertThat(RenderLayer.BACKGROUND.name(), `is`("BACKGROUND"))
        assertThat(RenderLayer.DEFAULT.name(), `is`("DEFAULT"))

        assertTrue(RenderLayer.TOP.index() > RenderLayer.DEFAULT.index())
        assertTrue(RenderLayer.DEFAULT.index() > RenderLayer.BACKGROUND.index())
        assertTrue(RenderLayer.BACKGROUND.index() > RenderLayer.BOTTOM.index())
    }
}