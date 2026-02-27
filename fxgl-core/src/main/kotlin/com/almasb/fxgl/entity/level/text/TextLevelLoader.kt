/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.level.text

import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.GameWorld
import com.almasb.fxgl.entity.SpawnData
import com.almasb.fxgl.entity.level.Level
import com.almasb.fxgl.entity.level.LevelLoader
import java.net.URL

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TextLevelLoader
@JvmOverloads constructor(
        val blockWidth: Int,
        val blockHeight: Int,
        val emptyChar: Char = ' '
) : LevelLoader {

    override fun load(url: URL, world: GameWorld): Level {
        val lines = url.openStream().bufferedReader().readLines()

        val entities = ArrayList<Entity>()

        var maxWidth = 0

        lines.forEachIndexed { i, line ->

            if (line.length > maxWidth)
                maxWidth = line.length

            line.forEachIndexed { j, c ->
                if (c != emptyChar) {
                    val e = world.create("$c", SpawnData(j.toDouble() * blockWidth, i.toDouble() * blockHeight))

                    entities.add(e)
                }
            }
        }

        return Level(maxWidth * blockWidth, lines.size * blockWidth, entities)
    }
}