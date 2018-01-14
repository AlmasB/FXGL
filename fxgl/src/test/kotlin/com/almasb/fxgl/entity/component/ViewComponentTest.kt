/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.component

import com.almasb.fxgl.app.FXGLMock
import com.almasb.fxgl.entity.RenderLayer
import javafx.scene.shape.Rectangle
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ViewComponentTest {

    private lateinit var view: ViewComponent

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            FXGLMock.mock()
        }
    }

    @BeforeEach
    fun setUp() {
        view = ViewComponent()
    }

    @Test
    fun `Setting a view does not affect render layer`() {
        val layer1 = object : RenderLayer {
            override fun index(): Int {
                return 10
            }

            override fun name(): String {
                return ""
            }
        }

        view.renderLayer = layer1
        view.setView(Rectangle())

        assertThat(view.renderLayer, `is`<RenderLayer>(layer1))
    }
}