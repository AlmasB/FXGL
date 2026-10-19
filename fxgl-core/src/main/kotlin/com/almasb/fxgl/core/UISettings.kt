/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty

/**
 * Cross-cutting runtime UI configuration accessible from any FXGL module.
 *
 * Lives in fxgl-core rather than on the FXGL facade because subsystems in
 * fxgl-scene (and potentially other downstream modules) need to consume
 * these properties at render time, and module direction prohibits an
 * upward reference to the fxgl facade from fxgl-scene.
 *
 * The FXGL facade exposes thin @JvmStatic accessors that delegate here,
 * so the public API stays unchanged.
 *
 * See issue #1224.
 */
object UISettings {

    /**
     * Global UI font size multiplier. All dialog and notification text is
     * scaled by this factor at render time. Default is 1.0 (no scaling).
     * Can be modified at runtime; bound listeners will update rendered
     * fonts on change.
     */
    val uiFontSizeMultiplier: DoubleProperty = SimpleDoubleProperty(1.0)
}