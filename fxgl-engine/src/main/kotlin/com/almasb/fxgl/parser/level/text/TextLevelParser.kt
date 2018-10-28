/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.parser.level.text

import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.entity.EntityFactory
import com.almasb.fxgl.entity.EntitySpawner
import com.almasb.fxgl.entity.level.Level
import com.almasb.fxgl.entity.level.LevelParser
import java.io.InputStream

/**
 * Parser for levels represented by plain text.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class TextLevelParser : LevelParser {

    companion object {
        private val log = Logger.get<TextLevelParser>()
    }

    private val producers = hashMapOf<Char, EntitySpawner>()

    /**
     * The empty (ignored) character.
     */


    init {
//        ReflectionUtils.findMethodsMapToFunctions(entityFactory, SpawnSymbol::class.java, EntitySpawner::class.java)
//                .forEach { producers[it.key.value] = it.value }
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
        producers[character] = producer
    }

    override fun parse(stream: InputStream, factory: EntityFactory): Level {
        return parse(stream.reader().readLines(), factory)
    }

    /**
     * Parses a file with given lines into a Level object.
     * The file must be located under "assets/text/". Only
     * the name of the file without the "assets/text/" is required.
     * It will be loaded by assetLoader.loadText() method.
     *
     * @return parsed Level
     */
    override fun parse(lines: List<String>, factory: EntityFactory): Level {
        return Level(-1, -1, listOf())
//        val emptyChar: Char = factory.emptyChar()
//
//
//        val entities = ArrayList<Entity>()
//
//        var maxWidth = 0
//
//        lines.forEachIndexed { i, line ->
//
//            if (line.length > maxWidth)
//                maxWidth = line.length
//
//            line.forEachIndexed { j, c ->
//                val producer = producers[c]
//                if (producer != null) {
//
//                    val e = tryCatchRoot { producer.apply(SpawnData(j.toDouble() * entityFactory.blockWidth(), i.toDouble() * entityFactory.blockHeight())) }
//                    entities.add(e)
//
//                } else if (c != emptyChar) {
//                    log.warning("No producer found for character: $c")
//                }
//            }
//        }
//
//        return Level(maxWidth * entityFactory.blockWidth(), lines.size * entityFactory.blockHeight(), entities)
    }
}