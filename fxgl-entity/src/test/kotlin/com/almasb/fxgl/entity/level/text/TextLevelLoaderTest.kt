/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.level.text

import com.almasb.fxgl.entity.*
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
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TextLevelLoaderTest {

    companion object {
        private val BLOCK_WIDTH = 40
        private val BLOCK_HEIGHT = 40
    }

    @Test
    fun `Default char is empty char`() {
        val loader = TextLevelLoader(BLOCK_WIDTH, BLOCK_HEIGHT)

        assertThat(loader.blockWidth, `is`(BLOCK_WIDTH))
        assertThat(loader.blockHeight, `is`(BLOCK_HEIGHT))
        assertThat(loader.emptyChar, `is`(' '))
    }

    @Test
    fun `Load text level`() {
        val loader = TextLevelLoader(BLOCK_WIDTH, BLOCK_HEIGHT, '0')

        val world = GameWorld()
        world.addEntityFactory(TestEntityFactory())

        val level = loader.load(javaClass.getResource("test_level.txt"), world)

        assertThat(level.width, `is`(4 * BLOCK_WIDTH))
        assertThat(level.height, `is`(5 * BLOCK_HEIGHT))
        assertThat(level.entities.size, `is`(4))

        assertThat(level.entities, hasItem(EntityMatcher(0, 2, EntityType.TYPE1)))
        assertThat(level.entities, hasItem(EntityMatcher(1, 2, EntityType.TYPE1)))
        assertThat(level.entities, hasItem(EntityMatcher(3, 0, EntityType.TYPE2)))
        assertThat(level.entities, hasItem(EntityMatcher(3, 4, EntityType.TYPE3)))
    }

    private enum class EntityType {
        TYPE1, TYPE2, TYPE3
    }

    @Test
    fun `Throw if file not found`() {
        assertThrows(IllegalStateException::class.java, {
            TextLevelLoader(BLOCK_WIDTH, BLOCK_HEIGHT).load(javaClass.getResource("bla-bla"), GameWorld())
        })
    }

    private class EntityMatcher(val x: Int, val y: Int, val entityType: EntityType) : BaseMatcher<Entity>() {

        override fun matches(item: Any): Boolean {
            val position = (item as Entity).position

            return position.x.toInt() == x*BLOCK_WIDTH && position.y.toInt() == y*BLOCK_HEIGHT && item.isType(entityType)
        }

        override fun describeTo(description: Description) {
            description.appendText("Entity at $x,$y with type $entityType")
        }
    }

    class TestEntityFactory : EntityFactory {

        @Spawns("1")
        fun newType1(data: SpawnData): Entity {
            val e = Entity()
            e.position = Point2D(data.x, data.y)
            e.type = EntityType.TYPE1
            return e
        }

        @Spawns("2")
        fun newType2(data: SpawnData): Entity {
            val e = Entity()
            e.position = Point2D(data.x, data.y)
            e.type = EntityType.TYPE2
            return e
        }

        @Spawns("3")
        fun newType3(data: SpawnData): Entity {
            val e = Entity()
            e.position = Point2D(data.x, data.y)
            e.type = EntityType.TYPE3
            return e
        }
    }
}