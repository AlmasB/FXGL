/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.parser.tiled

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsIterableContainingInOrder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TMXParserTest {

    @Test
    fun parse() {
        val map = javaClass.getResourceAsStream("/assets/tmx/sewers.tmx").use {
            TMXParser().parse(it)
        }

        // TODO: complete
        assertThat(map.tiledversion, `is`("1.1.2"))
        assertThat(map.width, `is`(64))
        assertThat(map.height, `is`(64))
        assertThat(map.tilewidth, `is`(24))
        assertThat(map.tileheight, `is`(24))
        assertThat(map.nextobjectid, `is`(4))
        assertThat(map.orientation, `is`("orthogonal"))
        assertThat(map.renderorder, `is`("right-down"))
        assertFalse(map.infinite)

        val tileset = map.tilesets[0]

        assertThat(tileset.firstgid, `is`(1))
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
        val layer3 = map.layers[2]
        val layer4 = map.layers[3]
    }
}