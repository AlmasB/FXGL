/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.parser.text

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.tryCatchRoot
import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.core.reflect.ReflectionUtils
import com.almasb.fxgl.entity.*
import com.almasb.fxgl.parser.LevelParser
import java.util.*

/**
 * Parser for levels represented by plain text.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class TextLevelParser(val entityFactory: TextEntityFactory) : LevelParser {

    constructor(emptyChar: Char, blockWidth: Int, blockHeight: Int) : this(object : TextEntityFactory {
        override fun emptyChar() = emptyChar

        override fun blockWidth() = blockWidth

        override fun blockHeight() = blockHeight
    })

    companion object {
        private val log = Logger.get("FXGL.TextLevelParser")
    }

    private val producers = HashMap<Char, EntitySpawner>()

    /**
     * The empty (ignored) character.
     */
    val emptyChar: Char

    init {
        emptyChar = entityFactory.emptyChar()

        ReflectionUtils.findMethodsMapToFunctions(entityFactory, SpawnSymbol::class.java, EntitySpawner::class.java)
                .forEach { producers.put(it.key.value, it.value) }
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
    fun addEntityProducer(character: Char, producer: EntitySpawner) {
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
    override fun parse(levelFileName: String): Level {
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

                    val e = tryCatchRoot { producer.apply(SpawnData(j.toDouble() * entityFactory.blockWidth(), i.toDouble() * entityFactory.blockHeight())) }
                    entities.add(e)

                } else if (c != emptyChar) {
                    log.warning("No producer found for character: " + c)
                }
            }
        }

        return Level(maxWidth * entityFactory.blockWidth(), lines.size * entityFactory.blockHeight(), entities)
    }
}