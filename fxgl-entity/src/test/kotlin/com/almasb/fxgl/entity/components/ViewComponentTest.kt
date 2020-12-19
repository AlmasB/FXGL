/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.entity.components

import com.almasb.fxgl.core.View
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.component.ComponentHelper
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.shape.Rectangle
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class ViewComponentTest {

    private lateinit var view: ViewComponent

    @BeforeEach
    fun setUp() {
        view = ViewComponent()
    }

    @ParameterizedTest
    @MethodSource("childProvider")
    fun `Add and remove children`(node: Node) {
        assertThat(view.children.size, `is`(0))

        view.addChild(node)

        assertThat(view.children.size, `is`(1))
        assertThat(view.children[0], `is`<Node>(node))

        view.removeChild(node)

        assertThat(view.children.size, `is`(0))
    }

    @Test
    fun `Add children with and without transforms`() {
        assertThat(view.children.size, `is`(0))

        val rect = Rectangle()
        val rect2 = Rectangle()

        view.addChild(rect, isTransformApplied = true)
        view.addChild(rect2, isTransformApplied = false)

        val e = Entity()

        ComponentHelper.setEntity(view, e)
        view.onAdded()

        assertThat(rect.parent.transforms.size, `is`(4))
        assertThat(rect2.parent.transforms.size, `is`(0))
    }

    @Test
    fun `View type children are correctly updated onUpdate and disposed on remove`() {
        val child = TestView()

        view.addChild(child)

        assertFalse(child.isDisposed)
        assertFalse(child.isUpdated)

        view.onUpdate(0.016)

        assertFalse(child.isDisposed)
        assertTrue(child.isUpdated)

        view.onRemoved()

        assertTrue(child.isDisposed)
    }

    @Test
    fun `Clear children`() {
        assertThat(view.children.size, `is`(0))

        val child = TestView()
        val child2 = TestView()

        view.addChild(child)
        view.addChild(child2, false)

        view.addChild(Rectangle())
        view.addChild(Rectangle(), false)

        assertThat(view.children.size, `is`(4))
        assertFalse(child.isDisposed)
        assertFalse(child2.isDisposed)

        view.clearChildren()

        assertThat(view.children.size, `is`(0))
        assertTrue(child.isDisposed)
        assertTrue(child2.isDisposed)
    }

    @Test
    fun `Opacity is set to node only and not parent`() {
        val rect = Rectangle()

        view.addChild(rect)

        assertThat(view.opacity, `is`(1.0))

        view.opacity = 0.35

        assertThat(view.opacity, `is`(0.35))
        assertThat(view.parent.opacity, `is`(1.0))
    }

    @Test
    fun `Visibility is set to view and the parent`() {
        assertTrue(view.isVisible)
        assertTrue(view.parent.isVisible)
        assertTrue(view.visibleProperty.value)

        view.isVisible = false

        assertFalse(view.isVisible)
        assertFalse(view.parent.isVisible)
        assertFalse(view.visibleProperty.value)
    }

    @Test
    fun `Add remove general click listener`() {
        var count = 0

        val l = EventHandler<MouseEvent> { count++ }

        view.addEventHandler(MouseEvent.MOUSE_CLICKED, l)

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

        view.removeEventHandler(MouseEvent.MOUSE_CLICKED, l)

        view.parent.fireEvent(e1)

        assertThat(count, `is`(1))
    }

    @Test
    fun `Add remove on click listener`() {
        var count = 0

        val l = EventHandler<MouseEvent> { count++ }

        view.addOnClickHandler(l)

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

        view.removeOnClickHandler(l)

        view.parent.fireEvent(e1)

        assertThat(count, `is`(1))
    }

    companion object {
        @Suppress("UNUSED")
        @JvmStatic
        fun childProvider(): Stream<Node> {
            return Stream.of(Rectangle(), TestView())
        }
    }

    private class TestView : Parent(), View {
        var isDisposed = false
        var isUpdated = false

        override fun onUpdate(tpf: Double) {
            isUpdated = true
        }

        override fun getNode(): Node {
            return this
        }

        override fun dispose() {
            isDisposed = true
        }
    }
}