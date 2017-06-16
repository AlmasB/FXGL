/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.algorithm

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
enum class TileType {
    WATER, EARTH;

    override fun toString(): String {
        return name.substring(0, 1)
    }
}