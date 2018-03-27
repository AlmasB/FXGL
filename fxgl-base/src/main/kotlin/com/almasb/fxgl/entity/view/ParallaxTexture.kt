/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.view

import com.almasb.fxgl.texture.Texture

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
data class ParallaxTexture(val texture: Texture, val speed: Double) {

    val image = texture.image

    internal var sx = 0.0
    internal var sy = 0.0
}