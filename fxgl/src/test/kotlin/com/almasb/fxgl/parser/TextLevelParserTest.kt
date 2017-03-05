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
import com.almasb.fxgl.entity.Entities
import com.almasb.fxgl.entity.TextEntityFactory
import com.almasb.fxgl.annotation.SpawnSymbol
import com.almasb.fxgl.app.MockApplicationModule
import com.almasb.fxgl.entity.EntitySpawner
import com.almasb.fxgl.entity.SpawnData
import com.almasb.fxgl.parser.text.TextLevelParser
import org.hamcrest.BaseMatcher
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.Description
import org.junit.Assert.assertThat
import org.junit.BeforeClass
import org.junit.Test

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

        @BeforeClass
        @JvmStatic fun before() {
            FXGL.configure(MockApplicationModule.get())
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Throw if file not found`() {
        TextLevelParser(' ', BLOCK_WIDTH, BLOCK_HEIGHT).parse("bla-bla")
    }

    @Test
    fun `Parse text file into level`() {
        val parser = TextLevelParser('0', BLOCK_WIDTH, BLOCK_HEIGHT)
        with(parser) {
            addEntityProducer('1', EntitySpawner { data -> Entities.builder().type(EntityType.TYPE1).from(data).build() })
            addEntityProducer('2', EntitySpawner { data -> Entities.builder().type(EntityType.TYPE2).from(data).build() })
            addEntityProducer('3', EntitySpawner { data -> Entities.builder().type(EntityType.TYPE3).from(data).build() })
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
            val position = Entities.getPosition(item as Entity)
            val type = Entities.getType(item)

            return position.x.toInt() == x*BLOCK_WIDTH && position.y.toInt() == y*BLOCK_HEIGHT && type.isType(entityType)
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
        fun newType1(data: SpawnData) = Entities.builder().type(EntityType.TYPE1).from(data).build()

        @SpawnSymbol('2')
        fun newType2(data: SpawnData) = Entities.builder().type(EntityType.TYPE2).from(data).build()

        @SpawnSymbol('3')
        fun newType3(data: SpawnData) = Entities.builder().type(EntityType.TYPE3).from(data).build()
    }
}