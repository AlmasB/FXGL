/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui

import javafx.scene.control.Slider
import javafx.scene.effect.BoxBlur

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FXGLSlider : Slider() {

    init {
        styleClass.add("fxgl-slider")

        effect = BoxBlur(1.2, 1.2, 2)

        valueProperty().addListener { _, _, newValue ->
            val percentage: Double = 100.0 * newValue.toDouble() / max

            // solution from https://stackoverflow.com/questions/51343759/how-to-change-fill-color-of-slider-in-javafx
            // in the String format,
            // %1$.1f%% gives the first format argument ("1$"),
            // i.e. percentage, formatted to 1 decimal place (".1f").
            // Note literal % signs must be escaped ("%%")

            val style = String.format(
                    "-track-color: linear-gradient(to right, " +
                    "-active-color 0.5%%, " +
                    "-active-color %1$.1f%%, " +
                    "-inactive-color %1$.1f%%, " +
                    "-inactive-color 100%%);",
                    percentage
            )

            setStyle(style)
        }
    }
}