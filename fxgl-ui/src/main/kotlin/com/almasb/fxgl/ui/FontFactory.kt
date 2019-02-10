/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui

import javafx.scene.text.Font

/**
 * A convenience wrapper for native JavaFX Font.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FontFactory constructor(private val font: Font) {

    /**
     * Construct new native JavaFX font with given size.
     * The font used is the same as the one used in factory
     * construction.
     *
     * @param size font size
     * @return font
     */
    fun newFont(size: Double) = Font(font.name, size)
}
