/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.parser

import com.almasb.fxgl.entity.Level

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface LevelParser {

    fun parse(levelFileName: String): Level
}