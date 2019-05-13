/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components

import com.almasb.fxgl.entity.EntityView
import javafx.scene.Node
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.shape.Rectangle
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ViewComponentTest {

    private lateinit var view: ViewComponent

    @BeforeEach
    fun setUp() {
        view = ViewComponent()
    }

    @Test
    fun `Set view from node`() {
        val rect = Rectangle()

        assertThat(view.view.node, `is`(not<Node>(rect)))

        view.setViewFromNode(rect)

        assertThat((view.view.node as EntityView).nodes[0], `is`<Node>(rect))
    }

    @Test
    fun `Opacity is set to node only and not parent`() {
        val rect = Rectangle()

        assertThat(view.view.node, `is`(not<Node>(rect)))

        view.setViewFromNode(rect)

        val entityView = view.view

        assertThat(entityView.node.opacity, `is`(1.0))

        view.opacity = 0.35

        assertThat(view.opacity, `is`(0.35))
        assertThat(entityView.node.opacity, `is`(0.35))
        assertThat(view.parent.opacity, `is`(1.0))
    }

    @Test
    fun `Add remove click listener`() {
        var count = 0

        val l = object : ClickListener {
            override fun onClick() {
                count++
            }
        }

        view.addClickListener(l)

        val e0 = MouseEvent(MouseEvent.MOUSE_PRESSED, 0.0, 0.0, 0.0, 0.0, MouseButton.PRIMARY, 1,
                false, false, false,
                false, false, false, false, false, false, false, null)

        val e1 = MouseEvent(MouseEvent.MOUSE_CLICKED, 0.0, 0.0, 0.0, 0.0, MouseButton.PRIMARY, 1,
                false, false, false,
                false, false, false, false, false, false, false, null)

        assertThat(count, `is`(0))

        // PRESS does not trigger click
        view.parent.fireEvent(e0)

        assertThat(count, `is`(0))

        view.parent.fireEvent(e1)

        assertThat(count, `is`(1))

        view.removeClickListener(l)

        view.parent.fireEvent(e1)

        assertThat(count, `is`(1))
    }
}