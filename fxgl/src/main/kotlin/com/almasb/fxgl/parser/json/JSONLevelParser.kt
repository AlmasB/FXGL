/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.parser.json

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.core.reflect.ReflectionUtils
import com.almasb.fxgl.entity.EntityFactory
import com.almasb.fxgl.entity.EntitySpawner
import com.almasb.fxgl.entity.SpawnData
import com.almasb.fxgl.entity.Spawns
import com.almasb.fxgl.gameplay.Level
import com.almasb.fxgl.parser.LevelParser
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class JSONLevelParser(private val entityFactory: EntityFactory) : LevelParser {

    private val producers = HashMap<String, EntitySpawner>()

    init {
        ReflectionUtils.findMethodsMapToFunctions(entityFactory, Spawns::class.java, EntitySpawner::class.java)
                .forEach { producers.put(it.key.value, it.value) }
    }

    override fun parse(levelFileName: String): Level {
        val stream = FXGL.getAssetLoader().getStream("/assets/json/$levelFileName")
        val jsonWorld = ObjectMapper().readValue<JSONWorld>(stream, JSONWorld::class.java)
        stream.close()

        val entities = jsonWorld.entities.map {
            val spawner = producers[it.name] ?: throw RuntimeException("@Spawns(${it.name}) method not found!")

            spawner.spawn(SpawnData(it.x, it.y))
        }

        return Level(0, 0, entities)
    }
}