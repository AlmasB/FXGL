/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.algorithm.procedural

import com.almasb.fxgl.core.collection.Grid

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface MapGenerator<T> {

    fun generate(width: Int, height: Int): Grid<T>
}