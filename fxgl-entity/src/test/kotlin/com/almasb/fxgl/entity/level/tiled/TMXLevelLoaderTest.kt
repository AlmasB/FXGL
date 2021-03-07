/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.level.tiled

import com.almasb.fxgl.entity.*
import com.almasb.fxgl.entity.components.IDComponent
import com.almasb.fxgl.test.RunWithFX
import javafx.geometry.Point2D
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import javafx.scene.shape.Polygon
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
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

    @ParameterizedTest
    @CsvSource("map_with_gid_objects.tmx", "map_with_indented_csv_data.tmx", "map_with_gzip_data.tmx")
    fun `Load tmx level with gid objects`(mapName: String) {
        val world = GameWorld()
        world.addEntityFactory(MyEntityFactory())

        val level = TMXLevelLoader().load(javaClass.getResource(mapName), world)

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

        // check that IDComponent was automatically added
        intArrayOf(14, 15, 17, 18, 28, 31, 33).forEach { id ->
            assertThat(objects.find { it.getInt("id") == id }!!.getComponent(IDComponent::class.java).id, `is`(id))
        }

        assertThat(level.properties.keys().size, `is`(1))
        assertThat(level.properties.getInt("testInt"), `is`(33))
    }

    @Test
    fun `Load tmx level with incorrect image paths`() {
        val world = GameWorld()
        world.addEntityFactory(MyEntityFactory())

        val level = TMXLevelLoader().load(javaClass.getResource("map_incorrect_image_path.tmx"), world)

        assertThat(level.width, `is`(16*10))
        assertThat(level.height, `is`(16*10))

        // 1 bg + 7 entities
        assertThat(level.entities.size, `is`(1 + 7))
    }

    @Test
    fun `Load tmx level with separate tile images`() {
        val world = GameWorld()

        val level = TMXLevelLoader().load(javaClass.getResource("tile_images/map_with_separate_tile_images.tmx"), world)

        val layerEntity = level.entities[0]

        val view = layerEntity.viewComponent.children[0] as ImageView

        assertThat(view.image.width, `is`(64 * 22.0))
        assertThat(view.image.height, `is`(64 * 22.0))

        assertThat(view.image.pixelReader.getColor(64*0 + 32, 32), `is`(not(Color.TRANSPARENT)))
        assertThat(view.image.pixelReader.getColor(64*1 + 32, 32), `is`(not(Color.TRANSPARENT)))
        assertThat(view.image.pixelReader.getColor(64*2 + 32, 32), `is`(not(Color.TRANSPARENT)))
        assertThat(view.image.pixelReader.getColor(64*3 + 32, 32), `is`(not(Color.TRANSPARENT)))

        assertThat(view.image.pixelReader.getColor(64*4 + 32, 32), `is`(Color.TRANSPARENT))
    }

    @Test
    fun `Load tmx level with different tileset sizes`() {
        val world = GameWorld()
        world.addEntityFactory(MyEntityFactory())

        val level = TMXLevelLoader().load(javaClass.getResource("complex/test_map.tmx"), world)

        val portal = level.entities.find { it.type == "portal" }!!

        val view = portal.viewComponent.children[0] as ImageView

        assertThat(view.image.width, `is`(128.0))
        assertThat(view.image.height, `is`(64.0))
    }

    @Test
    fun `Load tmx level with text objects`() {
        val world = GameWorld()
        world.addEntityFactory(MyTextObjectFactory())

        val level = TMXLevelLoader().load(javaClass.getResource("map_with_text_objects.tmx"), world)

        val text1 = level.entities.find { it.type == "textRed" }!!
        val text2 = level.entities.find { it.type == "textBlue" }!!
        val text3 = level.entities.find { it.type == "someType" }!!
        val text4 = level.entities.find { it.type == "emptyText" }!!

        assertThat(text1.getString("text"), `is`("Third piece of text"))
        assertThat(text1.getObject<Color>("color"), `is`(Color.rgb(255, 85, 0)))

        assertThat(text2.getString("text"), `is`("Hello World from Tiled to FXGL 11!"))
        assertThat(text2.getObject<Color>("color"), `is`(Color.rgb(85, 0, 255)))

        assertThat(text3.getString("text"), `is`("Another text"))
        assertThat(text3.getObject<Color>("color"), `is`(Color.rgb(0, 0, 0)))

        assertThat(text4.getString("text"), `is`(""))
        assertThat(text4.getObject<Color>("color"), `is`(Color.rgb(0, 0, 0)))
    }

    @Test
    fun `Load tmx level with flipped tiles`() {
        val world = GameWorld()
        world.addEntityFactory(MyTextObjectFactory())

        val level = TMXLevelLoader().load(javaClass.getResource("map_with_flipped_tiles.tmx"), world)

        val layerEntity = level.entities[0]

        val view = layerEntity.viewComponent.children[0] as ImageView

        // Horizontally-flipped
        assertThat(view.image.pixelReader.getColor(0,0), `is`((Color.TRANSPARENT)))
        assertThat(view.image.pixelReader.getColor(0,15), `is`((Color.BLACK)))

        assertThat(view.image.pixelReader.getColor(16,0), `is`((Color.BLACK)))
        assertThat(view.image.pixelReader.getColor(16,15), `is`((Color.TRANSPARENT)))

        // Vertically-flipped
        assertThat(view.image.pixelReader.getColor(0,16), `is`((Color.TRANSPARENT)))
        assertThat(view.image.pixelReader.getColor(15,16), `is`((Color.BLACK)))

        assertThat(view.image.pixelReader.getColor(16,16), `is`((Color.BLACK)))
        assertThat(view.image.pixelReader.getColor(31,16), `is`((Color.TRANSPARENT)))
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
        assertThat(tileset.spacing, `is`(0))
        assertThat(tileset.margin, `is`(0))
        assertThat(tileset.transparentcolor, `is`("ff00ff"))

        val layer1 = map.layers[0]

        assertThat(layer1.name, `is`("Bottom"))
        assertThat(layer1.width, `is`(64))
        assertThat(layer1.height, `is`(64))

        val layer1data = Files.readAllLines(Paths.get(javaClass.getResource("sewers_data.txt").toURI()))[0]
                .split(", ")
                .map { it.toLong() }

        assertThat(layer1.data, `is`(layer1data))

        val layer2 = map.layers[1]

        assertThat(layer2.name, `is`("Top"))
        assertThat(layer2.width, `is`(64))
        assertThat(layer2.height, `is`(64))
        assertThat(layer2.opacity, `is`(0.49f))

        val layer2data = Files.readAllLines(Paths.get(javaClass.getResource("sewers_data.txt").toURI()))[1]
                .split(", ")
                .map { it.toLong() }

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

    @Test
    fun `Load tmx level with spacing and margin`() {
        val mapName = "pac_v1_2_3.tmx"
        val map = javaClass.getResourceAsStream(mapName).use {
            TMXLevelLoader().parse(it)
        }

        assertThat(map.tiledversion, `is`(mapName.substringAfter("_v").substringBefore(".").replace("_", ".")))
        assertThat(map.orientation, `is`("orthogonal"))
        assertThat(map.renderorder, `is`("right-down"))
        assertFalse(map.infinite)

        val tileset = map.tilesets[0]

        assertThat(tileset.firstgid, `is`(1))
        assertThat(tileset.name, `is`("pac_tileset"))
        assertThat(tileset.image, `is`("pac_tileset.png"))
        assertThat(tileset.imagewidth, `is`(358))
        assertThat(tileset.imageheight, `is`(295))
        assertThat(tileset.tilewidth, `is`(20))
        assertThat(tileset.tileheight, `is`(20))
        assertThat(tileset.tilecount, `is`(238))
        assertThat(tileset.columns, `is`(17))
        assertThat(tileset.spacing, `is`(1))
        assertThat(tileset.margin, `is`(1))
        assertThat(tileset.transparentcolor, `is`(""))
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

        @Spawns("char")
        fun newCharacter(data: SpawnData): Entity {
            return Entity()
        }

        @Spawns("player")
        fun newPlayer(data: SpawnData): Entity {
            return Entity()
        }

        @Spawns("item")
        fun newItem(data: SpawnData): Entity {
            return Entity()
        }

        @Spawns("nav")
        fun newWalkableCell(data: SpawnData): Entity {
            return Entity()
        }

        @Spawns("portal")
        fun newPortal(data: SpawnData): Entity {
            return Entity().also { it.type = "portal" }
        }

        @Spawns("cellSelection")
        fun newCellSelection(data: SpawnData): Entity {
            return Entity()
        }
    }

    class MyTextObjectFactory : EntityFactory {
        @Spawns("someType,textRed,textBlue,emptyText")
        fun newText(data: SpawnData): Entity {
            return Entity().also { it.type = data.get("type") }
        }
    }
}