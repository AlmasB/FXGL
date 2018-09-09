/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components

import org.junit.jupiter.api.BeforeEach

class ViewComponentTest {

    private lateinit var view: ViewComponent

    @BeforeEach
    fun setUp() {
        view = ViewComponent()
    }

//    @Test
//    fun `Creation`() {
//        val rect = Rectangle()
//        val v = ViewComponent(rect)
//
//        assertThat(v.view.nodes, contains<Node>(rect))
//
//        val v1 = ViewComponent(RenderLayer.BACKGROUND)
//
//        assertThat(v1.renderLayer, `is`(RenderLayer.BACKGROUND))
//
//        val v2 = ViewComponent(rect, RenderLayer.BACKGROUND)
//
//        assertThat(v2.view.nodes, contains<Node>(rect))
//        assertThat(v2.renderLayer, `is`(RenderLayer.BACKGROUND))
//    }

//    @Test
//    fun `Setting a view does not affect render layer`() {
//        val layer1 = object : RenderLayer() {
//            override fun index(): Int {
//                return 10
//            }
//
//            override fun name(): String {
//                return ""
//            }
//        }
//
//        view.renderLayer = layer1
//        view.setView(Rectangle())
//
//        assertThat(view.renderLayer, `is`<RenderLayer>(layer1))
//        assertThat(view.renderLayerProperty().value, `is`<RenderLayer>(layer1))
//    }
}