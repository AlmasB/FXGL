/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input.virtual

import com.almasb.fxgl.input.Input
import javafx.beans.binding.Bindings
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.input.KeyCode
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.*
import javafx.scene.text.Font
import javafx.scene.text.Text

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class VirtualInput(private val input: Input) {

    fun createView(): Group = Group(createViewUp(), createViewRight(), createViewDown(), createViewLeft()).apply {
        val offset = 45.0

        val up = children[0]
        val right = children[1]
        val down = children[2]
        val left = children[3]

        val dpadCenter = Point2D(offset, offset)

        up.translateX = dpadCenter.x
        up.translateY = dpadCenter.y - offset

        down.translateX = dpadCenter.x
        down.translateY = dpadCenter.y + offset

        left.translateX = dpadCenter.x - offset
        left.translateY = dpadCenter.y

        right.translateX = dpadCenter.x + offset
        right.translateY = dpadCenter.y

        opacity = 0.7
    }

    protected fun createViewAndAttachEventHandler(btn: VirtualButton): Node {
        return createView(btn).also {
            it.setOnMousePressed {
                pressVirtual(btn)
            }

            it.setOnMouseReleased {
                releaseVirtual(btn)
            }
        }
    }

    abstract fun createViewUp(): Node
    abstract fun createViewRight(): Node
    abstract fun createViewDown(): Node
    abstract fun createViewLeft(): Node

    abstract fun createView(btn: VirtualButton): Node

    fun pressVirtual(btn: VirtualButton) {
        input.pressVirtual(btn)
    }

    fun releaseVirtual(btn: VirtualButton) {
        input.releaseVirtual(btn)
    }
}

abstract class VirtualDpad(input: Input) : VirtualInput(input) {

    override fun createViewUp(): Node = createViewAndAttachEventHandler(VirtualButton.UP)
    override fun createViewRight(): Node = createViewAndAttachEventHandler(VirtualButton.RIGHT)
    override fun createViewDown(): Node = createViewAndAttachEventHandler(VirtualButton.DOWN)
    override fun createViewLeft(): Node = createViewAndAttachEventHandler(VirtualButton.LEFT)
}

abstract class VirtualController(input: Input) : VirtualInput(input) {

    override fun createViewUp(): Node = createViewAndAttachEventHandler(VirtualButton.Y)
    override fun createViewRight(): Node = createViewAndAttachEventHandler(VirtualButton.B)
    override fun createViewDown(): Node = createViewAndAttachEventHandler(VirtualButton.A)
    override fun createViewLeft(): Node = createViewAndAttachEventHandler(VirtualButton.X)
}

abstract class VirtualMenuKey(private val input: Input, private val key: KeyCode, private val isMenuEnabled: Boolean) {

    internal fun createViewAndAttachHandler(): Node = createView().apply {
        setOnMousePressed {
            if (isMenuEnabled) {
                input.mockKeyPressEvent(key)
                input.mockKeyReleaseEvent(key)
            } else {
                input.mockKeyPress(key)
                input.mockKeyRelease(key)
            }
        }
    }

    abstract fun createView(): Node
}

class FXGLVirtualMenuKey(input: Input, key: KeyCode, isMenuEnabled: Boolean) : VirtualMenuKey(input, key, isMenuEnabled) {

    override fun createView(): Node {
        val bg = Circle(40.0, Color.web("blue", 0.15))
        bg.strokeType = StrokeType.OUTSIDE
        bg.stroke = Color.web("blue", 0.25)
        bg.strokeWidth = 3.0
        bg.centerX = 40.0
        bg.centerY = 40.0

        val rect1 = Rectangle(10.0, 30.0, Color.WHITE)
        rect1.translateX = 25.0
        rect1.translateY = 25.0
        rect1.arcWidth = 10.0
        rect1.arcHeight = 5.0

        val rect2 = Rectangle(10.0, 30.0, Color.WHITE)
        rect2.translateX = 45.0
        rect2.translateY = 25.0
        rect2.arcWidth = 10.0
        rect2.arcHeight = 5.0

        return Group(bg, rect1, rect2)
    }
}

/**
 * Default virtual dpad.
 */
class FXGLVirtualDpad(input: Input) : VirtualDpad(input) {

    override fun createView(btn: VirtualButton): Node {
        val root = StackPane()

        val bg = Circle(25.0, Color.BLACK)

        val triangle = Polygon(
                20.0, 10.0,
                35.0, 35.0,
                5.0, 35.0
        )

        triangle.fill = Color.WHITE

        triangle.fillProperty().bind(
                Bindings.`when`(root.pressedProperty()).then(Color.GRAY).otherwise(Color.WHITE)
        )

        when (btn) {
            VirtualButton.LEFT -> { triangle.rotate = -90.0 }
            VirtualButton.RIGHT -> { triangle.rotate = 90.0 }
            VirtualButton.UP -> {}
            VirtualButton.DOWN -> { triangle.rotate = 180.0 }
            else -> {}
        }

        root.children.addAll(bg, triangle)

        return root
    }
}

/**
 * Default virtual controller.
 */
class XboxVirtualController(input: Input) : VirtualController(input) {

    override fun createView(btn: VirtualButton): Node {
        val root = StackPane()

        val color = when (btn) {
            VirtualButton.X -> Color.BLUE
            VirtualButton.Y -> Color.YELLOW
            VirtualButton.A -> Color.GREEN
            VirtualButton.B -> Color.RED
            else -> Color.BLACK
        }

        val bg0 = Circle(25.0, color.darker())
        val bg1 = Circle(18.0, Color.TRANSPARENT)

        val text = Text(btn.toString())
        text.fill = Color.WHITE
        text.font = Font.font(26.0)

        root.children.addAll(bg0, bg1, text)

        bg1.fillProperty().bind(
                Bindings.`when`(root.pressedProperty()).then(color).otherwise(Color.TRANSPARENT)
        )

        return root
    }
}

class PSVirtualController(input: Input) : VirtualController(input) {

    override fun createView(btn: VirtualButton): Node {
        val root = StackPane()

        val node = when (btn) {
            VirtualButton.X -> Rectangle(26.0, 26.0, null).apply {
                stroke = Color.PINK
                strokeWidth = 2.0
            }

            VirtualButton.Y -> Polygon(
                    13.0, 0.0,
                    26.0, 22.0,
                    0.0, 22.0
            ).apply {
                translateY = -2.5
                stroke = Color.BLUEVIOLET
                strokeWidth = 2.0
                fill = null
            }

            VirtualButton.A -> Group().apply {

                val line1 = Line(0.0, 0.0, 26.0, 26.0)
                val line2 = Line(26.0, 0.0, 0.0, 26.0)

                line1.stroke = Color.LIGHTBLUE
                line2.stroke = Color.LIGHTBLUE

                line1.strokeWidth = 1.5
                line2.strokeWidth = 1.5

                children.addAll(line1, line2)
            }

            VirtualButton.B -> Circle(14.0, null).apply {
                stroke = Color.RED
                strokeWidth = 2.0
            }

            else -> Group()
        }

        val bg0 = Circle(25.0, Color.color(0.1, 0.1, 0.1))
        val bg1 = Circle(18.0, Color.TRANSPARENT)

        root.children.addAll(bg0, bg1, node)

//        bg1.fillProperty().bind(
//                Bindings.`when`(root.pressedProperty()).then(color).otherwise(Color.TRANSPARENT)
//        )

        return root
    }
}