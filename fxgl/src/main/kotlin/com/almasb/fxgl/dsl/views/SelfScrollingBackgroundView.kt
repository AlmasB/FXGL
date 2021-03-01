/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.views

import com.almasb.fxgl.texture.ScrollingView
import javafx.geometry.Orientation
import javafx.scene.image.Image

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SelfScrollingBackgroundView
@JvmOverloads constructor(
        image: Image,
        viewWidth: Double,
        viewHeight: Double,
        orientation: Orientation = Orientation.HORIZONTAL,

        var moveSpeed: Double
) : ScrollingView(image, viewWidth, viewHeight, orientation) {

    override fun onUpdate(tpf: Double) {
        if (orientation == Orientation.HORIZONTAL) {
            scrollX += moveSpeed * tpf
        } else {
            scrollY += moveSpeed * tpf
        }
    }
}
