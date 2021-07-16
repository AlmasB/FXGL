/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.animation

import javafx.beans.property.DoubleProperty
import javafx.geometry.Point3D

/**
 * An object whose position, scale, rotation and opacity can be animated.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface Animatable {

    fun xProperty(): DoubleProperty
    fun yProperty(): DoubleProperty
    fun zProperty(): DoubleProperty

    fun scaleXProperty(): DoubleProperty
    fun scaleYProperty(): DoubleProperty
    fun scaleZProperty(): DoubleProperty

    fun rotationXProperty(): DoubleProperty
    fun rotationYProperty(): DoubleProperty
    fun rotationZProperty(): DoubleProperty

    fun opacityProperty(): DoubleProperty

    fun setScaleOrigin(pivotPoint: Point3D)
    fun setRotationOrigin(pivotPoint: Point3D)
}