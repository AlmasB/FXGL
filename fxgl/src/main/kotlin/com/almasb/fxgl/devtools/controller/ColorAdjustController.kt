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
        FXGL.getLogger(javaClass).infof("Hue:[%.2f], Saturation:[%.2f], Brightness:[%.2f], Contrast:[%.2f]",
                sliderHue.value, sliderSaturation.value, sliderBrightness.value, sliderContrast.value)
    }
}