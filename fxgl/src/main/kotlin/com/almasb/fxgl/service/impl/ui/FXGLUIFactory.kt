/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.service.impl.ui

import com.almasb.fxgl.asset.FXGLAssets
import com.almasb.fxgl.service.UIFactory
import com.almasb.fxgl.ui.FXGLButton
import com.almasb.fxgl.ui.FXGLCheckBox
import com.almasb.fxgl.ui.FXGLChoiceBox
import com.almasb.fxgl.ui.FXGLSpinner
import com.google.inject.Inject
import javafx.collections.ObservableList
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Spinner
import javafx.scene.text.Font

/**
 * FXGL provider of UI factory service.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGLUIFactory
@Inject
private constructor() : UIFactory {

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
}