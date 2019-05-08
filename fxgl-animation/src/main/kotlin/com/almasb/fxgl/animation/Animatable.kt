/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.animation

import javafx.beans.property.DoubleProperty

/**
 * An object whose position, scale, rotation and opacity can be animated.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface Animatable {

    fun xProperty(): DoubleProperty
    fun yProperty(): DoubleProperty

    fun scaleXProperty(): DoubleProperty
    fun scaleYProperty(): DoubleProperty

    fun rotationProperty(): DoubleProperty

    fun opacityProperty(): DoubleProperty
}