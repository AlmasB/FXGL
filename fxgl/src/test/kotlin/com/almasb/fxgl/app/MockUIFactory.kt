/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.ui.*
import javafx.beans.binding.StringBinding
import javafx.beans.binding.StringExpression
import javafx.collections.ObservableList
import javafx.scene.control.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object MockUIFactory : UIFactory {
    override fun registerFontFactory(type: FontType?, fontFactory: FontFactory?) {
    }

    override fun newFont(type: FontType?, size: Double): Font {
        return Font.font(size)
    }

    override fun newText(message: String?): Text {
        return Text()
    }

    override fun newText(message: String?, fontSize: Double): Text {
        return Text()
    }

    override fun newText(message: String?, textColor: Color?, fontSize: Double): Text {
        return Text()
    }

    override fun newText(textBinding: StringExpression?): Text {
        return Text()
    }

    override fun newTextFlow(): FXGLTextFlow {
        return FXGLTextFlow()
    }

    override fun newWindow(): MDIWindow {
        return MDIWindow()
    }

    override fun <T : Any?> newListView(items: ObservableList<T>?): ListView<T> {
        return ListView()
    }

    override fun <T : Any?> newListView(): ListView<T> {
        return ListView()
    }

    override fun newFont(size: Double): Font {
        return Font.font(size)
    }

    override fun newButton(text: String?): Button {
        return Button(text)
    }

    override fun newButton(text: StringBinding): Button {
        val btn = FXGLButton(text.value)
        btn.textProperty().bind(text)
        return btn
    }

    override fun <T : Any?> newChoiceBox(items: ObservableList<T>?): ChoiceBox<T> {
        return ChoiceBox(items)
    }

    override fun <T : Any?> newChoiceBox(): ChoiceBox<T> {
        return ChoiceBox()
    }

    override fun newCheckBox(): CheckBox {
        return CheckBox()
    }

    override fun <T : Any?> newSpinner(items: ObservableList<T>?): Spinner<T> {
        return Spinner(items)
    }
}