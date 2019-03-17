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