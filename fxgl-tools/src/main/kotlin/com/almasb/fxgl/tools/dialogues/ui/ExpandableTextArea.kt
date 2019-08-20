/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues.ui

import javafx.beans.binding.Bindings
import javafx.scene.control.TextArea
import javafx.scene.text.Text
import java.util.concurrent.Callable
import kotlin.math.abs

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ExpandableTextArea(prefWidth: Double, prefHeight: Double) : TextArea() {

    /**
     * Dummy text to keep track of approximate text height.
     */
    private val text = Text()

    private var prevHeight = prefHeight

    init {
        styleClass.add("dialogue-editor-text-area")

        this.prefWidth = prefWidth
        this.prefHeight = prefHeight

        isWrapText = true

        text.opacity = 0.0
        text.fontProperty().bind(fontProperty())
        text.textProperty().bind(textProperty())
        text.wrappingWidth = prefWidth - 30.0

        prefHeightProperty().bind(
                Bindings.createDoubleBinding(Callable { computeHeight() }, text.layoutBoundsProperty())
        )
    }

    private fun computeHeight(): Double {
        // TODO: this should probably be based on font size?
        var newHeight = text.layoutBounds.height + 20.0
        newHeight = if (abs(prevHeight - newHeight) > 15) newHeight else prevHeight

        prevHeight = newHeight

        return newHeight
    }
}