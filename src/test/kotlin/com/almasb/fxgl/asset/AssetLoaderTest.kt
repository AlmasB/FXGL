/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.asset

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.MockServicesModule
import com.almasb.fxgl.ui.UIController
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
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

    private val JS_DATA = "function test() {\n" + "    return \"JSTest\"\n" + "}\n"

    private lateinit var assetLoader: AssetLoader

    companion object {
        @BeforeClass
        @JvmStatic fun before() {
            FXGL.mockServices(MockServicesModule())
        }
    }

    @Before
    fun setUp() {
        assetLoader = FXGL.getInstance(AssetLoader::class.java)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Exception is thrown if asset not found`() {
        assetLoader.getStream("nothing.jpg")
    }

    @Test
    fun loadTexture() {
        val texture = assetLoader.loadTexture("brick.png")

        assertThat(texture.image.width, `is`(64.0))
        assertThat(texture.image.height, `is`(64.0))

        // test cache
        val texture2 = assetLoader.loadTexture("brick.png")
        assertThat(texture2.image, `is`(texture.image))

        texture.dispose()
        texture2.dispose()
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
        assertThat(sound.clip, `is`(notNullValue()))
    }

    @Test
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
    fun loadScript() {
        val script = assetLoader.loadScript("test.js")

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

    }

    @Test
    fun loadAppIcon() {

    }

    @Test
    fun loadBehaviorTree() {

    }

    @Test
    fun getStream() {
        val stream = assetLoader.getStream("/assets/scripts/test.js")

        assertThat(stream, `is`(notNullValue()))

        stream.close()
    }
}