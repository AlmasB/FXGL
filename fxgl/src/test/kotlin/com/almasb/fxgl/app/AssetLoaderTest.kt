/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.test.RunWithFX
import com.almasb.fxgl.ui.UIController
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@ExtendWith(RunWithFX::class)
class AssetLoaderTest {

    private val TEXT_ASSETS = arrayOf("test1.txt")
    private val TEXT_DATA = arrayOf("Lorem ipsum dolor sit amet, consectetuer adipiscing elit.\n" +
            "Aenean commodo ligula eget dolor.\n" +
            "Aenean massa.\n" +
            "Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.\n" +
            "Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem.\n" +
            "Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu.\n" +
            "In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo.\n" +
            "Nullam dictum felis eu pede mollis pretium.\n" +
            "Integer tincidunt.\n" +
            "Cras dapibus.\n" +
            "Vivamus elementum semper nisi.\n" +
            "Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim.\n" +
            "Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet.\n" +
            "Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui.\n" +
            "Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing\n" +
            "sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante\n" +
            "tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet\n" +
            "rci eget eros faucibus tincidunt")

    private lateinit var assetLoader: AssetLoader

    @BeforeEach
    fun setUp() {
        assetLoader = AssetLoader()
    }

    @Test
    fun `Exception is thrown if asset not found`() {
        assertThrows(IllegalArgumentException::class.java, {
            assetLoader.getStream("nothing.jpg")
        })
    }

    @Test
    fun loadImage() {
        var image = assetLoader.loadImage("brick.png")

        assertThat(image.width, `is`(64.0))
        assertThat(image.height, `is`(64.0))

        image = assetLoader.loadImage("bla-bla")

        assertThat(image, `is`(notNullValue()))
    }

    @Test
    fun loadTexture() {
        var texture = assetLoader.loadTexture("brick.png")

        assertThat(texture.image.width, `is`(64.0))
        assertThat(texture.image.height, `is`(64.0))

        texture = assetLoader.loadTexture("bla-bla")

        assertThat(texture, `is`(notNullValue()))
    }

    @Test
    fun loadResizedTexture() {
        var texture = assetLoader.loadTexture("brick.png", 32.0, 32.0)

        assertThat(texture.image.width, `is`(32.0))
        assertThat(texture.image.height, `is`(32.0))

        assertTrue(texture.image === assetLoader.loadTexture("brick.png", 32.0, 32.0).image)

        texture.dispose()

        texture = assetLoader.loadTexture("bla-bla", 32.0, 32.0)

        assertThat(texture, `is`(notNullValue()))
    }

    @Test
    fun loadSound() {
        var sound = assetLoader.loadSound("intro.wav")

        assertThat(sound, `is`(notNullValue()))
        assertTrue(sound === assetLoader.loadSound("intro.wav"))

        sound = assetLoader.loadSound("bla-bla")

        assertThat(sound, `is`(notNullValue()))
    }

    @Test
    fun loadMusic() {
        // Note: the loading might fail on linux if missing libavformat for jfxmedia, but dummy music object should be loaded
        var music = assetLoader.loadMusic("intro.mp3")

        assertThat(music, `is`(notNullValue()))

        music.dispose()

        music = assetLoader.loadMusic("bla-bla")

        assertThat(music, `is`(notNullValue()))
    }

    @Test
    fun loadText() {
        for (i in TEXT_ASSETS.indices) {
            val textAsset = TEXT_ASSETS[i]
            val actualLines = assetLoader.loadText(textAsset)
            val expectedLines = TEXT_DATA[i].split("\n".toRegex()).dropLastWhile { it.isEmpty() }

            assertThat(actualLines, `is`(expectedLines))
        }

        val lines = assetLoader.loadText("bla-bla")

        assertThat(lines.size, `is`(0))

        assertTrue(assetLoader.loadText("test1.txt") === assetLoader.loadText("test1.txt"))
    }

    @Test
    fun loadResourceBundle() {
        val resourceBundle = assetLoader.loadResourceBundle("test.properties")
        val resourceBundle2 = assetLoader.loadResourceBundle("test.properties")

        assertTrue(resourceBundle === resourceBundle2)

        assertThat(resourceBundle, `is`(notNullValue()))
        assertThat(resourceBundle.getString("testKey"), `is`("testValue"))

        val bundle = assetLoader.loadResourceBundle("bla-bla")
        assertThat(bundle, `is`(notNullValue()))
    }

    @Test
    fun loadCursorImage() {
        var cursorImage = assetLoader.loadCursorImage("test_cursor.png")

        assertThat(cursorImage, `is`(notNullValue()))
        assertThat(cursorImage.width, `is`(64.0))
        assertThat(cursorImage.height, `is`(64.0))

        cursorImage = assetLoader.loadCursorImage("bla-bla")

        assertThat(cursorImage, `is`(notNullValue()))
    }

    @Test
    fun loadUI() {
        var count = 0
        val controller = object : UIController {
            override fun init() {
                count++
            }
        }

        var ui = assetLoader.loadUI("test_ui.fxml", controller)

        assertThat(ui, `is`(notNullValue()))
        assertThat(ui.root, `is`(notNullValue()))
        assertThat(count, `is`(1))

        ui = assetLoader.loadUI("bla-bla", controller)

        assertThat(ui, `is`(notNullValue()))
        assertThat(ui.root, `is`(notNullValue()))
        assertThat(count, `is`(1))
    }

    @Test
    fun loadCSS() {
        var css = assetLoader.loadCSS("test.css")

        assertThat(css, `is`(notNullValue()))

        css = assetLoader.loadCSS("bla-bla")

        assertThat(css, `is`(notNullValue()))
    }

    @Test
    fun loadFont() {
        var fontFactory = assetLoader.loadFont("test.ttf")

        assertThat(fontFactory, `is`(notNullValue()))

        val fontFactory2 = assetLoader.loadFont("test.ttf")

        assertTrue(fontFactory === fontFactory2)

        val font = fontFactory.newFont(18.0)

        assertThat(font, `is`(notNullValue()))
        assertThat(font.family, `is`("Elektra"))
        assertThat(font.name, `is`("Elektra"))
        assertThat(font.size, `is`(18.0))

        fontFactory = assetLoader.loadFont("bla-bla")

        assertThat(fontFactory, `is`(notNullValue()))
        assertThat(fontFactory.newFont(18.0), `is`(notNullValue()))
    }

    @Test
    fun getStream() {
        val stream = assetLoader.getStream("/assets/scripts/test.js")

        assertThat(stream, `is`(notNullValue()))

        stream.close()
    }

    @Test
    fun `getStream throws if no valid stream`() {
        assertThrows(IllegalArgumentException::class.java) {
            assetLoader.getStream("bla-bla")
        }
    }

    @Test
    fun `Check loaded from cache when present`() {
        // ensure cache is clean
        assetLoader.clearCache()

        val texture = assetLoader.loadTexture("brick.png")
        val texture2 = assetLoader.loadTexture("brick.png")

        assertThat(texture2.image, `is`(texture.image))
        
        assetLoader.clearCache()

        val texture3 = assetLoader.loadTexture("brick.png")

        assertThat(texture3.image, `is`(not(texture.image)))

        texture.dispose()
        texture2.dispose()
        texture3.dispose()

        assetLoader.clearCache()
    }
}
