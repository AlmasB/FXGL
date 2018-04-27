/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.asset

import com.almasb.fxgl.app.FXGLMock
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.ui.UIController
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS
import java.util.*

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
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

    private val JS_DATA = "function test() {\n" + "    return \"JSTest\";\n" + "}\n"

    private lateinit var assetLoader: AssetLoader

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            FXGLMock.mock()
        }
    }

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
        val image = assetLoader.loadImage("brick.png")

        assertThat(image.width, `is`(64.0))
        assertThat(image.height, `is`(64.0))
    }

    @Test
    fun loadTexture() {
        val texture = assetLoader.loadTexture("brick.png")

        assertThat(texture.image.width, `is`(64.0))
        assertThat(texture.image.height, `is`(64.0))
    }

    @Test
    fun loadResizedTexture() {
        val texture = assetLoader.loadTexture("brick.png", 32.0, 32.0)

        assertThat(texture.image.width, `is`(32.0))
        assertThat(texture.image.height, `is`(32.0))

        texture.dispose()
    }

    @Test
    fun loadSound() {
        val sound = assetLoader.loadSound("intro.wav")

        assertThat(sound, `is`(notNullValue()))
    }

    @Test
    // setting up potentially missing libavformat for jfxmedia is an overkill, so just skip
    @DisabledOnOs(OS.LINUX)
    fun loadMusic() {
        val music = assetLoader.loadMusic("intro.mp3")

        assertThat(music, `is`(notNullValue()))

        music.dispose()
    }

    @Test
    fun loadText() {
        for (i in TEXT_ASSETS.indices) {
            val textAsset = TEXT_ASSETS[i]
            val actualLines = assetLoader.loadText(textAsset)
            val expectedLines = Arrays.asList(*TEXT_DATA[i].split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())

            assertThat(actualLines, `is`(expectedLines))
        }
    }

    @Test
    fun loadKV() {
        val kv = assetLoader.loadKV("test.kv")

        assertThat(kv, `is`(notNullValue()))

        val charInfo = kv.`to`(CharInfo::class.java)

        assertThat(charInfo, `is`(notNullValue()))
        assertThat(charInfo.hp, `is`(50))
        assertThat(charInfo.mana, `is`(30.0))
        assertThat(charInfo.name, `is`("TestName"))
        assertThat(charInfo.killable, `is`(true))
    }

    @Test
    fun loadScriptRaw() {
        val script = assetLoader.loadScriptRaw("test.js")

        assertThat(script, `is`(JS_DATA))
    }

    @Test
    fun loadResourceBundle() {
        val resourceBundle = assetLoader.loadResourceBundle("test.properties")

        assertThat(resourceBundle, `is`(notNullValue()))
        assertThat(resourceBundle.getString("testKey"), `is`("testValue"))
    }

    @Test
    fun loadCursorImage() {
        val cursorImage = assetLoader.loadCursorImage("test_cursor.png")

        assertThat(cursorImage, `is`(notNullValue()))
        assertThat(cursorImage.width, `is`(64.0))
        assertThat(cursorImage.height, `is`(64.0))
    }

    @Test
    fun loadUI() {
        var count = 0
        val controller = object : UIController {
            override fun init() {
                count++
            }
        }

        val ui = assetLoader.loadUI("test_ui.fxml", controller)

        assertThat(ui, `is`(notNullValue()))
        assertThat(ui.root, `is`(notNullValue()))
        assertThat(count, `is`(1))
    }

    @Test
    fun loadCSS() {
        val css = assetLoader.loadCSS("test.css")

        assertThat(css, `is`(notNullValue()))
    }

    @Test
    fun loadFont() {
        val fontFactory = assetLoader.loadFont("test.ttf")

        assertThat(fontFactory, `is`(notNullValue()))

        val font = fontFactory.newFont(18.0)

        assertThat(font, `is`(notNullValue()))
        assertThat(font.family, `is`("Elektra"))
        assertThat(font.name, `is`("Elektra"))
        assertThat(font.size, `is`(18.0))
    }

    @Test
    fun loadBehaviorTree() {
        val tree = assetLoader.loadBehaviorTree<Entity>("test.tree")

        assertThat(tree, `is`(notNullValue()))
    }

    @Test
    fun getStream() {
        val stream = assetLoader.getStream("/assets/scripts/test.js")

        assertThat(stream, `is`(notNullValue()))

        stream.close()
    }

    @Test
    fun `Load all file names from given asset directory`() {
        val filenames = assetLoader.loadFileNames("/assets/ui/")

        assertThat(filenames,
                hasItems(
                "css/test.css",
                "cursors/test_cursor.png",
                "fonts/test.ttf",
                "test_ui.fxml"
                ))
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