/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.level.tiled

import com.almasb.fxgl.entity.*
import com.almasb.fxgl.test.RunWithFX
import javafx.geometry.Point2D
import javafx.scene.paint.Color
import javafx.scene.shape.Polygon
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.nio.file.Files
import java.nio.file.Paths

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@ExtendWith(RunWithFX::class)
class TMXLevelLoaderTest {

    @Test
    fun `Load tmx level`() {
        val world = GameWorld()
        world.addEntityFactory(MyEntityFactory())

        val level = TMXLevelLoader().load(javaClass.getResource("sewers_v1_2_3.tmx"), world)

        assertThat(level.width, `is`(24*64))
        assertThat(level.height, `is`(24*64))

        // 4 object entities + 2 background tile layers
        assertThat(level.entities.size, `is`(4 + 2))
    }

    @Test
    fun `Load tmx level with gid objects`() {
        val world = GameWorld()
        world.addEntityFactory(MyEntityFactory())

        val level = TMXLevelLoader().load(javaClass.getResource("map_with_gid_objects.tmx"), world)

        assertThat(level.width, `is`(16*10))
        assertThat(level.height, `is`(16*10))

        // 1 bg + 7 entities
        assertThat(level.entities.size, `is`(1 + 7))

        val objects = level.entities.drop(1)

        assertThat(objects.find { it.getInt("id") == 14 }!!.position, `is`(Point2D(0.0, 144.0)))
        assertThat(objects.find { it.getInt("id") == 15 }!!.position, `is`(Point2D(0.0, 0.0)))
        assertThat(objects.find { it.getInt("id") == 17 }!!.position, `is`(Point2D(144.0, 16.0)))
        assertThat(objects.find { it.getInt("id") == 18 }!!.position, `is`(Point2D(16.0, 0.0)))

        // Tiled y is up, our y is down hence the height subtraction
        assertThat(objects.find { it.getInt("id") == 28 }!!.position, `is`(Point2D(47.0, 80.0 - 16)))
        assertThat(objects.find { it.getInt("id") == 31 }!!.position, `is`(Point2D(111.0, 128.0 - 16)))
        assertThat(objects.find { it.getInt("id") == 33 }!!.position, `is`(Point2D(32.0, 47.0 - 16)))

        // check whether gid tiled objects have been parsed
        assertTrue(objects.find { it.getInt("id") == 28 }!!.viewComponent.children.isNotEmpty())
        assertTrue(objects.find { it.getInt("id") == 31 }!!.viewComponent.children.isNotEmpty())
        assertTrue(objects.find { it.getInt("id") == 33 }!!.viewComponent.children.isNotEmpty())
    }

    @ParameterizedTest
    @CsvSource("sewers_v1_1_2.tmx", "sewers_v1_2_3.tmx")
    fun parse(mapName: String) {
        val map = javaClass.getResourceAsStream(mapName).use {
            TMXLevelLoader().parse(it)
        }

        assertThat(map.tiledversion, `is`(mapName.substringAfter("_v").substringBefore(".").replace("_", ".")))
        assertThat(map.width, `is`(64))
        assertThat(map.height, `is`(64))
        assertThat(map.tilewidth, `is`(24))
        assertThat(map.tileheight, `is`(24))
        assertThat(map.nextobjectid, `is`(5))
        assertThat(map.orientation, `is`("orthogonal"))
        assertThat(map.renderorder, `is`("right-down"))
        assertFalse(map.infinite)

        val tileset = map.tilesets[0]

        assertThat(tileset.firstgid, `is`(1))
        assertThat(tileset.name, `is`("sewer_tileset"))
        assertThat(tileset.image, `is`("sewer_tileset.png"))
        assertThat(tileset.imagewidth, `is`(192))
        assertThat(tileset.imageheight, `is`(217))
        assertThat(tileset.tilewidth, `is`(24))
        assertThat(tileset.tileheight, `is`(24))
        assertThat(tileset.tilecount, `is`(72))
        assertThat(tileset.columns, `is`(8))
        assertThat(tileset.transparentcolor, `is`("ff00ff"))

        val layer1 = map.layers[0]

        assertThat(layer1.name, `is`("Bottom"))
        assertThat(layer1.width, `is`(64))
        assertThat(layer1.height, `is`(64))

        val layer1data = Files.readAllLines(Paths.get(javaClass.getResource("sewers_data.txt").toURI()))[0]
                .split(", ")
                .map { it.toInt() }

        assertThat(layer1.data, `is`(layer1data))

        val layer2 = map.layers[1]

        assertThat(layer2.name, `is`("Top"))
        assertThat(layer2.width, `is`(64))
        assertThat(layer2.height, `is`(64))
        assertThat(layer2.opacity, `is`(0.49f))

        val layer2data = Files.readAllLines(Paths.get(javaClass.getResource("sewers_data.txt").toURI()))[1]
                .split(", ")
                .map { it.toInt() }

        assertThat(layer2.data, `is`(layer2data))

        val layer3 = map.layers[2]

        assertThat(layer3.name, `is`("Collidables"))

        val obj1 = layer3.objects[0]
        val obj2 = layer3.objects[1]

        assertThat(obj1.id, `is`(1))
        assertThat(obj1.name, `is`("name1"))
        assertThat(obj1.type, `is`("type1"))
        assertThat(obj1.x, `is`(96))
        assertThat(obj1.y, `is`(0))
        assertThat(obj1.width, `is`(72))
        assertThat(obj1.height, `is`(336))
        assertThat(obj1.rotation, `is`(0.0f))
        assertThat(obj1.properties.size, `is`(4))

        assertThat(obj1.properties["collidable"] as Boolean, `is`(true))
        assertThat(obj1.properties["someColor"] as Color, `is`(Color.web("#ff55ff00")))
        assertThat(obj1.properties["someInt"] as Int, `is`(33))
        assertThat(obj1.properties["someString"] as String, `is`("Text Here"))

        assertThat(obj2.id, `is`(2))
        assertThat(obj2.name, `is`("name2"))
        assertThat(obj2.type, `is`("type2"))
        assertThat(obj2.x, `is`(504))
        assertThat(obj2.y, `is`(0))
        assertThat(obj2.width, `is`(360))
        assertThat(obj2.height, `is`(168))
        assertThat(obj2.rotation, `is`(31.0f))

        val obj4 = layer3.objects[2]

        assertThat(obj4.id, `is`(4))
        assertThat((obj4.properties["polygon"] as Polygon).points, `is`(Polygon(0.0, 0.0, 120.0, -72.0, 192.0, 48.0, 24.0, 48.0).points))

        val layer4 = map.layers[3]

        assertThat(layer4.name, `is`("Group2"))

        val obj3 = layer4.objects[0]

        assertThat(obj3.id, `is`(3))
        assertThat(obj3.name, `is`("no name"))
        assertThat(obj3.type, `is`(""))
        assertThat(obj3.x, `is`(240))
        assertThat(obj3.y, `is`(504))
        assertThat(obj3.width, `is`(120))
        assertThat(obj3.height, `is`(120))
    }

    class MyEntityFactory : EntityFactory {

        @Spawns("no_type,type1")
        fun newRectangle(data: SpawnData): Entity {
            return Entity()
        }

        @Spawns("")
        fun newEmpty(data: SpawnData): Entity {
            return Entity()
        }

        @Spawns("type2,type3,Wall,Player,Coin")
        fun newCircle(data: SpawnData): Entity {
            return Entity()
        }
    }
}