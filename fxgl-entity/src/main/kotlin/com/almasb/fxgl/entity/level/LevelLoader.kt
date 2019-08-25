/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */


package com.almasb.fxgl.entity.level

import com.almasb.fxgl.entity.GameWorld
import java.net.URL

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface LevelLoader {

    fun load(url: URL, world: GameWorld): Level
}