package com.almasb.fxgl.entity.level

import com.almasb.fxgl.entity.EntityFactory
import java.io.InputStream

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface LevelParser {

    fun parse(stream: InputStream, factory: EntityFactory): Level

    fun parse(lines: List<String>, factory: EntityFactory): Level
}