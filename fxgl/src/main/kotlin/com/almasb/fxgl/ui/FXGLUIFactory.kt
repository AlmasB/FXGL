/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui

import com.almasb.fxgl.asset.FXGLAssets
import javafx.collections.ObservableList
import javafx.scene.control.*
import javafx.scene.text.Font

/**
 * FXGL provider of UI factory service.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGLUIFactory : UIFactory {

    override fun newWindow(): MDIWindow {
        return MDIWindow()
    }

    override fun newFont(size: Double): Font {
        return FXGLAssets.UI_FONT.newFont(size)
    }

    override fun newButton(text: String): Button {
        return FXGLButton(text)
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