/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.FXGLMock
import com.almasb.fxgl.app.SystemPropertyKey
import com.almasb.fxgl.entity.RenderLayer
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
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
    fun `Creation`() {
        val rect = Rectangle()
        val v = ViewComponent(rect)

        assertThat(v.view.nodes, contains<Node>(rect))

        val v1 = ViewComponent(RenderLayer.BACKGROUND)

        assertThat(v1.renderLayer, `is`(RenderLayer.BACKGROUND))

        val v2 = ViewComponent(rect, RenderLayer.BACKGROUND)

        assertThat(v2.view.nodes, contains<Node>(rect))
        assertThat(v2.renderLayer, `is`(RenderLayer.BACKGROUND))
    }

    // TODO: complete
    @Test
    fun `Debug view`() {
        FXGL.getProperties().setValue(SystemPropertyKey.DEV_SHOWBBOX, true)
        FXGL.getProperties().setValue(SystemPropertyKey.DEV_BBOXCOLOR, Color.BLUE)
    }

    @Test
    fun `Setting a view does not affect render layer`() {
        val layer1 = object : RenderLayer() {
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
        assertThat(view.renderLayerProperty().value, `is`<RenderLayer>(layer1))
    }
}