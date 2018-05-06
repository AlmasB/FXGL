/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RenderLayerTest {

    @Test
    fun `Creation`() {
        val layer = RenderLayer(500)

        assertThat(layer.index(), `is`(500))
        assertThat(layer.toString(), `is`("NoName(500)"))
    }

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