/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.devtools.controller

import com.almasb.fxgl.app.FXGL
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