/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.parser

import com.almasb.fxgl.entity.*
import com.almasb.fxgl.parser.text.TextLevelParser
import javafx.geometry.Point2D
import org.hamcrest.BaseMatcher
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.Description
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TextLevelParserTest {

    private enum class EntityType {
        TYPE1, TYPE2, TYPE3
    }

    companion object {
        private val BLOCK_WIDTH = 40
        private val BLOCK_HEIGHT = 40
    }

    @Test
    fun `Throw if file not found`() {
        assertThrows(IllegalArgumentException::class.java, {
            TextLevelParser(' ', BLOCK_WIDTH, BLOCK_HEIGHT).parse("bla-bla")
        })
    }

    @Test
    fun `Parse text file into level`() {
        val parser = TextLevelParser('0', BLOCK_WIDTH, BLOCK_HEIGHT)
        with(parser) {
            addEntityProducer('1', EntitySpawner { data ->
                val e = Entity()
                e.position = Point2D(data.x, data.y)
                e.type = EntityType.TYPE1
                e
            })
            addEntityProducer('2', EntitySpawner { data ->
                val e = Entity()
                e.position = Point2D(data.x, data.y)
                e.type = EntityType.TYPE2
                e
            })
            addEntityProducer('3', EntitySpawner { data ->
                val e = Entity()
                e.position = Point2D(data.x, data.y)
                e.type = EntityType.TYPE3
                e
            })
        }

        val level = parser.parse("test_level.txt")

        assertThat(level.width, `is`(4 * BLOCK_WIDTH))
        assertThat(level.height, `is`(5 * BLOCK_HEIGHT))
        assertThat(level.entities.size, `is`(4))

        assertThat(level.entities, hasItem(EntityMatcher(0, 2, EntityType.TYPE1)))
        assertThat(level.entities, hasItem(EntityMatcher(1, 2, EntityType.TYPE1)))
        assertThat(level.entities, hasItem(EntityMatcher(3, 0, EntityType.TYPE2)))
        assertThat(level.entities, hasItem(EntityMatcher(3, 4, EntityType.TYPE3)))
    }

    @Test
    fun `Parse using given entity factory`() {
        val parser = TextLevelParser(TestEntityFactory())

        val level = parser.parse("test_level.txt")

        assertThat(level.width, `is`(4 * BLOCK_WIDTH))
        assertThat(level.height, `is`(5 * BLOCK_HEIGHT))
        assertThat(level.entities.size, `is`(4))

        assertThat(level.entities, hasItem(EntityMatcher(0, 2, EntityType.TYPE1)))
        assertThat(level.entities, hasItem(EntityMatcher(1, 2, EntityType.TYPE1)))
        assertThat(level.entities, hasItem(EntityMatcher(3, 0, EntityType.TYPE2)))
        assertThat(level.entities, hasItem(EntityMatcher(3, 4, EntityType.TYPE3)))
    }

    private class EntityMatcher(val x: Int, val y: Int, val entityType: EntityType) : BaseMatcher<Entity>() {

        override fun matches(item: Any): Boolean {
            val position = (item as Entity).positionComponent

            return position.x.toInt() == x*BLOCK_WIDTH && position.y.toInt() == y*BLOCK_HEIGHT && item.isType(entityType)
        }

        override fun describeTo(description: Description) {
            description.appendText("Entity at $x,$y with type $entityType")
        }
    }

    class TestEntityFactory : TextEntityFactory {
        override fun emptyChar(): Char {
            return '0'
        }

        override fun blockWidth(): Int {
            return BLOCK_WIDTH
        }

        override fun blockHeight(): Int {
            return BLOCK_HEIGHT
        }

        @SpawnSymbol('1')
        fun newType1(data: SpawnData): Entity {
            val e = Entity()
            e.position = Point2D(data.x, data.y)
            e.type = EntityType.TYPE1
            return e
        }

        @SpawnSymbol('2')
        fun newType2(data: SpawnData): Entity {
            val e = Entity()
            e.position = Point2D(data.x, data.y)
            e.type = EntityType.TYPE2
            return e
        }

        @SpawnSymbol('3')
        fun newType3(data: SpawnData): Entity {
            val e = Entity()
            e.position = Point2D(data.x, data.y)
            e.type = EntityType.TYPE3
            return e
        }
    }
}