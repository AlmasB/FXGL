/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.binding.Bindings
import javafx.scene.Cursor
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.util.Duration

/**
 * A lightweight (non-native) window.
 * To change window size, use setPrefSize().
 * To change window position, use layoutX and layoutY.
 *
 * Adapted from jfxtras-window.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
open class MDIWindow : Region() {

    private enum class ResizeMode {
        NONE,
        TOP,
        LEFT,
        BOTTOM,
        RIGHT,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    private var mouseX = 0.0
    private var mouseY = 0.0
    private var nodeX = 0.0
    private var nodeY = 0.0
    private var isDragging = false
    private var isAnimationFinished = true

    private var resizeMode = ResizeMode.NONE

    private var isResizeTop = false
    private var isResizeLeft = false
    private var isResizeBot = false
    private var isResizeRight = false

    private var prevHeight = 0.0

    private val header = TextFlow()

    private val minimizeButton = makeMinimizeButton()
    private val closeButton = makeCloseButton()

    private fun makeMinimizeButton(): Node {
        val pane = Pane()
        pane.translateY = 1.0

        val size = 20.0
        val offset = 3.0

        val line1 = Line(offset, size / 2 + 1, size, size / 2 + 1)
        val line2 = Line(size, offset, offset, size)

        line1.strokeWidth = 2.0
        line2.strokeWidth = 2.0

        val stroke = Bindings.`when`(pane.hoverProperty()).then(Color.BLUE).otherwise(Color.WHITE)

        line1.strokeProperty().bind(
                stroke
        )

        line2.strokeProperty().bind(
                stroke
        )

        line2.isVisible = false

        pane.children.addAll(line1, line2)

        return pane
    }

    private fun makeCloseButton(): Node {
        val pane = Pane()
        pane.translateY = 1.0

        val size = 22.0
        val offset = 3.0

        val line1 = Line(offset, offset, size, size)
        val line2 = Line(size, offset, offset, size)

        line1.strokeWidth = 2.0
        line2.strokeWidth = 2.0

        val stroke = Bindings.`when`(pane.hoverProperty()).then(Color.RED).otherwise(Color.WHITE)

        line1.strokeProperty().bind(
                stroke
        )

        line2.strokeProperty().bind(
                stroke
        )

        pane.children.addAll(line1, line2)

        return pane
    }

    private val root = Pane()
    var contentPane: Pane = StackPane()
        set(value) {
            field = value

            root.children.set(1, value)

            if (value.width < width) {
                width = value.width
            }
        }

    private var isMinimized = false

    // these allow window functionality customization

    var isManuallyResizable = true

    var isMovable = true

    var isMinimizable: Boolean
        get() = minimizeButton.isVisible
        set(value) { minimizeButton.isVisible = value }

    var isCloseable: Boolean
        get() = closeButton.isVisible
        set(value) { closeButton.isVisible = value }

    var title = "Untitled"
        set(value) {
            field = value

            updateTitle()
        }

    private fun updateTitle() {
        header.children.clear()
        header.children.add(Text(title).also { it.fill = Color.WHITE })
    }

    init {
        initMovableWindow()
        
        val box = HBox(header)
        box.translateY = -25.0
        box.styleClass.add("window-titlebar")
        box.prefWidthProperty().bind(this.widthProperty())

        initMinimizeButton(box)
        initCloseButton(box)
        
        root.children.add(box)
        root.children.add(contentPane)
        root.children.addAll(minimizeButton, closeButton)
        root.layoutY = 25.0

        children.add(root)
        
        background = Background(BackgroundFill(Color.BLACK, null, null))

        updateTitle()
    }

    private fun initMinimizeButton(box: HBox) {
        minHeight = 28.0

        minimizeButton.translateXProperty().bind(box.prefWidthProperty().subtract(54.0))
        minimizeButton.translateY = -20.0
        
        minimizeButton.setOnMouseClicked {
            if (!isAnimationFinished)
                return@setOnMouseClicked

            isMinimized = !isMinimized

            if (isMinimized) {
                contentPane.isVisible = false

                // remember our height before minimize effect, so we can expand correctly
                prevHeight = prefHeight
            }

            val animation = Timeline()
            animation.keyFrames.add(KeyFrame(Duration.seconds(0.25), KeyValue(
                    prefHeightProperty(), if (isMinimized) 28.0 else prevHeight
            )))

            animation.setOnFinished {
                if (!isMinimized)
                    contentPane.isVisible = true

                isAnimationFinished = true
            }

            animation.play()

            isAnimationFinished = false
        }
    }

    private fun initCloseButton(box: HBox) {
        closeButton.translateXProperty().bind(box.prefWidthProperty().subtract(27.0))
        closeButton.translateY = -22.0

        closeButton.setOnMouseClicked {
            if (parent is Group) {
                (parent as Group).children -= this
            } else {
                // TODO:
            }
        }
    }
    
    private fun initMovableWindow() {
        val n = this
        
        setOnMousePressed { event ->
            val parentScaleX = n.parent.localToSceneTransformProperty().value.mxx
            val parentScaleY = n.parent.localToSceneTransformProperty().value.myy

            mouseX = event.sceneX
            mouseY = event.sceneY

            nodeX = n.layoutX * parentScaleX
            nodeY = n.layoutY * parentScaleY
        }

        setOnMouseDragged { event ->
            val parentScaleX = n.parent.localToSceneTransformProperty().value.mxx
            val parentScaleY = n.parent.localToSceneTransformProperty().value.myy

            val boundsInScene = n.localToScene(n.boundsInLocal)

            val sceneX = boundsInScene.minX
            val sceneY = boundsInScene.minY

            val offsetX = event.sceneX - mouseX
            val offsetY = event.sceneY - mouseY

            if (resizeMode == ResizeMode.NONE && n.isMovable) {
                nodeX += offsetX
                nodeY += offsetY

                val scaledX = nodeX * 1 / parentScaleX
                val scaledY = nodeY * 1 / parentScaleY

                n.layoutX = scaledX
                n.layoutY = scaledY

                isDragging = true

            } else {

                if (isResizeTop) {
                    val insetOffset = insets.top / 2

                    val yDiff = sceneY / parentScaleY + insetOffset - event.sceneY / parentScaleY

                    val newHeight = n.prefHeight + yDiff

                    if (newHeight > n.minHeight(0.0)) {
                        n.layoutY = n.layoutY - yDiff
                        n.prefHeight = newHeight
                    }
                }

                if (isResizeLeft) {
                    val insetOffset = insets.left / 2

                    val xDiff = sceneX / parentScaleX + insetOffset - event.sceneX / parentScaleX

                    val newWidth = n.prefWidth + xDiff

                    if (newWidth > Math.max(n.minWidth(0.0), n.contentPane.minWidth(0.0))) {
                        n.layoutX = n.layoutX - xDiff
                        n.prefWidth = newWidth
                    }
                }

                if (isResizeBot) {
                    val insetOffset = insets.bottom / 2

                    val yDiff = (event.sceneY / parentScaleY - sceneY / parentScaleY - insetOffset)

                    var newHeight = yDiff

                    newHeight = Math.max(newHeight, n.minHeight(0.0))

                    if (newHeight < n.maxHeight(0.0)) {
                        n.prefHeight = newHeight
                    }
                }

                if (isResizeRight) {
                    val insetOffset = insets.right / 2

                    val xDiff = (event.sceneX / parentScaleX
                            - sceneX / parentScaleY - insetOffset)

                    var newWidth = xDiff

                    newWidth = Math.max(newWidth, Math.max(n.contentPane.minWidth(0.0), n.minWidth(0.0)))

                    if (newWidth < n.maxWidth(0.0)) {
                        n.prefWidth = newWidth
                    }
                }
            }

            mouseX = event.sceneX
            mouseY = event.sceneY
        }

        setOnMouseClicked {
            isDragging = false
        }

        setOnMouseMoved { t ->
            if (n.isMinimized || !n.isManuallyResizable) {

                isResizeTop = false
                isResizeLeft = false
                isResizeBot = false
                isResizeRight = false

                resizeMode = ResizeMode.NONE

                return@setOnMouseMoved
            }

            val scaleX = n.localToSceneTransformProperty().value.mxx
            val scaleY = n.localToSceneTransformProperty().value.myy

            val border = 5.0 * scaleX

            val diffMinX = Math.abs(n.layoutBounds.minX - t.x + insets.left)
            val diffMinY = Math.abs(n.layoutBounds.minY - t.y + insets.top)
            val diffMaxX = Math.abs(n.layoutBounds.maxX - t.x - insets.right)
            val diffMaxY = Math.abs(n.layoutBounds.maxY - t.y - insets.bottom)

            val left = diffMinX * scaleX < Math.max(border, insets.left / 2 * scaleX)
            val top = diffMinY * scaleY < Math.max(border, insets.top / 2 * scaleY)
            val right = diffMaxX * scaleX < Math.max(border, insets.right / 2 * scaleX)
            val bottom = diffMaxY * scaleY < Math.max(border, insets.bottom / 2 * scaleY)

            isResizeTop = false
            isResizeLeft = false
            isResizeBot = false
            isResizeRight = false

            if (left && !top && !bottom) {
                n.cursor = Cursor.W_RESIZE
                resizeMode = ResizeMode.LEFT
                isResizeLeft = true
            } else if (left && top && !bottom) {
                n.cursor = Cursor.NW_RESIZE
                resizeMode = ResizeMode.TOP_LEFT
                isResizeLeft = true
                isResizeTop = true
            } else if (left && !top && bottom) {
                n.cursor = Cursor.SW_RESIZE
                resizeMode = ResizeMode.BOTTOM_LEFT
                isResizeLeft = true
                isResizeBot = true
            } else if (right && !top && !bottom) {
                n.cursor = Cursor.E_RESIZE
                resizeMode = ResizeMode.RIGHT
                isResizeRight = true
            } else if (right && top && !bottom) {
                n.cursor = Cursor.NE_RESIZE
                resizeMode = ResizeMode.TOP_RIGHT
                isResizeRight = true
                isResizeTop = true
            } else if (right && !top && bottom) {
                n.cursor = Cursor.SE_RESIZE
                resizeMode = ResizeMode.BOTTOM_RIGHT
                isResizeRight = true
                isResizeBot = true
            } else if (top && !left && !right) {
                n.cursor = Cursor.N_RESIZE
                resizeMode = ResizeMode.TOP
                isResizeTop = true
            } else if (bottom && !left && !right) {
                n.cursor = Cursor.S_RESIZE
                resizeMode = ResizeMode.BOTTOM
                isResizeBot = true
            } else {
                n.cursor = Cursor.DEFAULT
                resizeMode = ResizeMode.NONE
            }

            n.autosize()
        }
    }
}