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

package com.almasb.fxgl.parser

import com.almasb.fxgl.ecs.Entity
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.gameplay.Level
import java.util.*

/**
 * Parser for levels represented by plain text.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class TextLevelParser(val entityFactory: EntityFactory) {

    constructor() : this(object : EntityFactory(' ') {})

    companion object {
        private val log = FXGL.getLogger("FXGL.TextLevelParser")
    }

    private val producers = HashMap<Char, (Int, Int) -> Entity>()

    /**
     * The empty (ignored) character.
     * If you don't set this, there will be a warning generated for
     * each such character.
     *
     * @defaultValue ' '
     */
    var emptyChar = ' '

    init {
        emptyChar = entityFactory.emptyChar

        for (method in entityFactory.javaClass.declaredMethods) {
            val producer = method.getDeclaredAnnotation(EntityProducer::class.java)
            if (producer != null) {
                producers[producer.value] = { x, y -> method.invoke(entityFactory, x, y) as Entity }
            }
        }
    }

    /**
     * Register a [producer] that generates an entity when a
     * [character] was found during parsing.
     *
     * Producer is (x: Int, y: Int) -> Entity
     *
     * @param x column position of character
     * @param y row position of character
     */
    fun addEntityProducer(character: Char, producer: (x: Int, y: Int) -> Entity) {
        producers.put(character, producer)
    }

    /**
     * Parses a file with given [levelFileName] into a Level object.
     * The file must be located under "assets/text/". Only
     * the name of the file without the "assets/text/" is required.
     * It will be loaded by assetLoader.loadText() method.
     *
     * @return parsed Level
     */
    fun parse(levelFileName: String): Level {
        val assetLoader = FXGL.getAssetLoader()
        val lines = assetLoader.loadText(levelFileName)

        val entities = ArrayList<Entity>()

        var maxWidth = 0

        for (i in lines.indices) {
            val line = lines[i]
            if (line.length > maxWidth)
                maxWidth = line.length

            for (j in 0 until line.length) {
                val c = line[j]
                val producer = producers[c]
                if (producer != null) {
                    val e = producer.invoke(j, i)
                    entities.add(e)
                } else if (c != emptyChar) {
                    log.warning("No producer found for character: " + c)
                }
            }
        }

        return Level(maxWidth, lines.size, entities)
    }
}