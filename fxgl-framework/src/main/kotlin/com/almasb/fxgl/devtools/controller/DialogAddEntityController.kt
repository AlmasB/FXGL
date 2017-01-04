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

package com.almasb.fxgl.devtools.controller

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.entity.Entities
import com.almasb.fxgl.ui.UIController
import javafx.fxml.FXML
import javafx.scene.control.ColorPicker
import javafx.scene.control.TextField
import javafx.scene.shape.Rectangle

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DialogAddEntityController : UIController {

    @FXML
    private lateinit var fieldPositionX: TextField
    @FXML
    private lateinit var fieldPositionY: TextField
    @FXML
    private lateinit var fieldRotation: TextField
    @FXML
    private lateinit var colorPicker: ColorPicker

    override fun init() {

    }

    fun onAdd() {
        val gameWorld = FXGL.getApp().gameWorld

        com.almasb.fxgl.entity.Entities.builder()
                .at(fieldPositionX.text.toDouble(), fieldPositionY.text.toDouble())
                .rotate(fieldRotation.text.toDouble())
                .viewFromNode(Rectangle(40.0, 40.0, colorPicker.value))
                .buildAndAttach(gameWorld)
    }
}