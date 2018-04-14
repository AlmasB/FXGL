/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.devtools.controller

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.ui.UIController
import javafx.fxml.FXML
import javafx.scene.control.Slider
import javafx.scene.effect.ColorAdjust

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ColorAdjustController : UIController {

    @FXML
    private lateinit var sliderHue: Slider
    @FXML
    private lateinit var sliderSaturation: Slider
    @FXML
    private lateinit var sliderBrightness: Slider
    @FXML
    private lateinit var sliderContrast: Slider

    private val colorAdjust = ColorAdjust()

    override fun init() {
        FXGL.getApp().gameScene.effect = colorAdjust

        colorAdjust.hueProperty().bindBidirectional(sliderHue.valueProperty())
        colorAdjust.saturationProperty().bindBidirectional(sliderSaturation.valueProperty())
        colorAdjust.brightnessProperty().bindBidirectional(sliderBrightness.valueProperty())
        colorAdjust.contrastProperty().bindBidirectional(sliderContrast.valueProperty())
    }

    fun onPrintValues() {
        Logger.get(javaClass).infof("Hue:[%.2f], Saturation:[%.2f], Brightness:[%.2f], Contrast:[%.2f]",
                sliderHue.value, sliderSaturation.value, sliderBrightness.value, sliderContrast.value)
    }
}