/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui

import com.almasb.fxgl.ui.FontType.UI
import javafx.beans.binding.StringBinding
import javafx.beans.binding.StringExpression
import javafx.collections.ObservableList
import javafx.scene.control.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text

/**
 * FXGL provider of UI factory service.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGLUIFactory : UIFactory {

    private val fontFactories = hashMapOf<FontType, FontFactory>()

    override fun registerFontFactory(type: FontType, fontFactory: FontFactory) {
        fontFactories[type] = fontFactory
    }

    override fun newText(textBinding: StringExpression): Text {
        val text = newText(textBinding.get())
        text.textProperty().bind(textBinding)
        return text
    }

    override fun newText(message: String, textColor: Color, fontSize: Double): Text {
        val text = Text(message)
        text.fill = textColor
        text.font = newFont(fontSize)
        return text
    }

    override fun newText(message: String, fontSize: Double): Text {
        return newText(message, Color.WHITE, fontSize)
    }

    override fun newText(message: String): Text {
        return newText(message, Color.WHITE, 18.0)
    }

    override fun newTextFlow(): FXGLTextFlow {
        return FXGLTextFlow()
    }

    override fun newWindow(): MDIWindow {
        return MDIWindow()
    }

    override fun newFont(size: Double): Font {
        return newFont(UI, size)
    }

    override fun newFont(type: FontType, size: Double): Font
            = fontFactories[type]?.newFont(size) ?: throw IllegalStateException("No font factory found for $type")

    override fun newButton(text: String): Button {
        return FXGLButton(text)
    }

    override fun newButton(text: StringBinding): Button {
        val btn = FXGLButton(text.value)
        btn.textProperty().bind(text)
        return btn
    }

    override fun <T> newChoiceBox(items: ObservableList<T>): ChoiceBox<T> {
        return FXGLChoiceBox(items)
    }

    override fun <T> newChoiceBox(): ChoiceBox<T> {
        return FXGLChoiceBox()
    }

    override fun newCheckBox(): CheckBox {
        return FXGLCheckBox()
    }

    override fun <T> newSpinner(items: ObservableList<T>): Spinner<T> {
        return FXGLSpinner(items)
    }

    override fun <T : Any> newListView(items: ObservableList<T>): ListView<T> {
        return FXGLListView(items)
    }

    override fun <T : Any> newListView(): ListView<T> {
        return FXGLListView<T>()
    }
}