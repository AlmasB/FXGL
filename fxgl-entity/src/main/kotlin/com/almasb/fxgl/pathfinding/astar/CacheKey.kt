/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.astar

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal data class CacheKey(
        val startX: Int,
        val startY: Int,
        val endX: Int,
        val endY: Int
)