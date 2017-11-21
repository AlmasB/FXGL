/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui

import javafx.collections.ObservableList
import javafx.scene.control.*
import javafx.scene.text.Font

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object MockUIFactory : UIFactory {
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