/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.app

import com.almasb.fxgl.app.services.FXGLAssetLoaderService
import com.almasb.fxgl.audio.AudioPlayer
import com.almasb.fxgl.core.asset.AssetType
import com.almasb.fxgl.logging.ConsoleOutput
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.logging.LoggerConfig
import com.almasb.fxgl.logging.LoggerLevel
import com.almasb.fxgl.test.InjectInTest
import com.almasb.fxgl.test.RunWithFX
import com.almasb.fxgl.texture.getDummyImage
import com.almasb.fxgl.ui.UIController
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.lang.ClassCastException
import java.lang.invoke.MethodHandles

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@ExtendWith(RunWithFX::class)
class AssetLoaderServiceTest {

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

    private lateinit var assetLoader: FXGLAssetLoaderService

    @BeforeEach
    fun setUp() {
        assetLoader = FXGLAssetLoaderService()

        InjectInTest.inject(MethodHandles.lookup(), assetLoader, "audioService", AudioPlayer())
        InjectInTest.inject(MethodHandles.lookup(), assetLoader, "userAppClass", AssetLoaderServiceTest::class.java)

        assetLoader.onInit()
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
    fun `Load image from URL`() {
        val image = assetLoader.loadImage(javaClass.getResource("test_icon.png"))

        assertThat(image.width, `is`(256.0))
        assertThat(image.height, `is`(256.0))
    }

    @Test
    fun `URL based loads are cached`() {
        val image1 = assetLoader.loadImage(javaClass.getResource("test_icon.png"))
        val image2 = assetLoader.loadImage(javaClass.getResource("test_icon.png"))

        assertThat(image1, `is`(not(getDummyImage())))
        assertTrue(image1 === image2)
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
    fun `Load texture from URL`() {
        val texture = assetLoader.loadTexture(javaClass.getResource("test_icon.png"))

        assertThat(texture.image.width, `is`(256.0))
        assertThat(texture.image.height, `is`(256.0))
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
    fun `Load sound from URL`() {
        val sound = assetLoader.loadSound(javaClass.getResource("/fxglassets/sounds/intro.wav"))

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
    fun `Load music from URL`() {
        val music = assetLoader.loadMusic(javaClass.getResource("/fxglassets/music/intro.mp3"))

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
    fun `Load Text from URL`() {
        val actualLines = assetLoader.loadText(javaClass.getResource("/fxglassets/text/test1.txt"))

        val expectedLines = TEXT_DATA[0].split("\n".toRegex()).dropLastWhile { it.isEmpty() }

        assertThat(actualLines, `is`(expectedLines))

        val lines = assetLoader.loadText("bla-bla")

        assertThat(lines.size, `is`(0))
    }

    @Test
    fun `Relative and absolute URL load calls cache the same object`() {
        // these are loaded from two different places (assets/text and fxglassets/text), so shouldn't be same object
        assertTrue(assetLoader.loadText("test1.txt") !== assetLoader.loadText(javaClass.getResource("/fxglassets/text/test1.txt")))

        // these are loaded from the same place, so should be same object
        assertTrue(assetLoader.loadImage("brick2.png") === assetLoader.loadImage(javaClass.getResource("/fxglassets/textures/brick2.png")))
    }

    @Test
    fun `Load DialogueGraph`() {
        val graph = assetLoader.loadDialogueGraph("simple.json")

        assertThat(graph.nodes.size, `is`(3))
        assertThat(graph.edges.size, `is`(2))

        assertThat(graph.startNode.text, `is`("Simple start."))
    }

    @Test
    fun `Load DialogueGraph from URL`() {
        val graph = assetLoader.loadDialogueGraph(javaClass.getResource("/fxglassets/dialogues/simple.json"))

        assertThat(graph.nodes.size, `is`(3))
        assertThat(graph.edges.size, `is`(2))

        assertThat(graph.startNode.text, `is`("Simple start."))
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
        assertThat(bundle.keySet().size, `is`(0))
    }

    @Test
    fun `Load ResourceBundle from URL`() {
        val resourceBundle = assetLoader.loadResourceBundle(javaClass.getResource("/fxglassets/properties/test.properties"))

        assertThat(resourceBundle, `is`(notNullValue()))
        assertThat(resourceBundle.getString("testKey"), `is`("testValue"))
    }

    @Test
    fun `Load PropertyMap`() {
        var map = assetLoader.loadPropertyMap("properties/test.properties")

        assertThat(map, `is`(notNullValue()))
        assertThat(map.getString("testKey"), `is`("testValue"))

        map = assetLoader.loadPropertyMap("bla-bla")
        assertThat(map, `is`(notNullValue()))
        assertThat(map.keys().size, `is`(0))
    }

    @Test
    fun `Load PropertyMap from URL`() {
        val map = assetLoader.loadPropertyMap(javaClass.getResource("/fxglassets/properties/test.properties"))

        assertThat(map, `is`(notNullValue()))
        assertThat(map.getString("testKey"), `is`("testValue"))
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

        // UI objects are not cached

        assertFalse(assetLoader.loadUI("test_ui.fxml", controller) === ui)

        assertThat(count, `is`(2))

        ui = assetLoader.loadUI("bla-bla", controller)

        assertThat(ui, `is`(notNullValue()))
        assertThat(ui.root, `is`(notNullValue()))
        assertThat(count, `is`(2))
    }

    @Test
    fun `Load UI from URL`() {
        val ui = assetLoader.loadUI(javaClass.getResource("/fxglassets/ui/test_ui.fxml"), object : UIController {
            override fun init() {
            }
        })

        assertThat(ui, `is`(notNullValue()))
        assertThat(ui.root, `is`(notNullValue()))
    }

    @Test
    fun loadCSS() {
        var css = assetLoader.loadCSS("test.css")

        assertThat(css, `is`(notNullValue()))

        css = assetLoader.loadCSS("bla-bla")

        assertThat(css, `is`(notNullValue()))
    }

    @Test
    fun `Load CSS from URL`() {
        val css = assetLoader.loadCSS(javaClass.getResource("/fxglassets/ui/css/test.css"))

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
    fun `Load Font from URL`() {
        val fontFactory = assetLoader.loadFont(javaClass.getResource("/fxglassets/ui/fonts/test.ttf"))

        assertThat(fontFactory, `is`(notNullValue()))

        val font = fontFactory.newFont(18.0)

        assertThat(font, `is`(notNullValue()))
        assertThat(font.family, `is`("Elektra"))
        assertThat(font.name, `is`("Elektra"))
        assertThat(font.size, `is`(18.0))
    }

    @Test
    fun `Load JSON`() {
        var obj = assetLoader.loadJSON("json/test.json", TestJSONData::class.java)

        assertTrue(obj.isPresent)
        assertThat(obj.get().name, `is`("TestName"))
        assertThat(obj.get().value, `is`(892))

        // invalid URL
        obj = assetLoader.loadJSON("bla-bla", TestJSONData::class.java)

        assertFalse(obj.isPresent)

        // try to load as diff incompatible object
        val obj2 = assetLoader.loadJSON("json/test.json", String::class.java)

        assertFalse(obj2.isPresent)
    }

    @Test
    fun `Load JSON from URL`() {
        val obj = assetLoader.loadJSON(javaClass.getResource("/fxglassets/json/test.json"), TestJSONData::class.java)

        assertTrue(obj.isPresent)
        assertThat(obj.get().name, `is`("TestName"))
        assertThat(obj.get().value, `is`(892))
    }

    @Test
    fun `Load obj Model3D`() {
        val model = assetLoader.loadModel3D("cube.obj")

        assertThat(model, `is`(notNullValue()))
    }

    @Test
    fun `Load obj Model3D from URL`() {
        val model = assetLoader.loadModel3D(javaClass.getResource("/fxglassets/models/cube.obj"))

        assertThat(model, `is`(notNullValue()))
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

    @Test
    fun `load throws ClassCastException if T does not match asset type`() {
        assertThrows(ClassCastException::class.java) {
            // try to load Image as String
            val s: String = assetLoader.load(AssetType.IMAGE, "brick.png")
        }
    }

    @Test
    fun `Loading a valid resource in wrong format does not crash`() {
        // try loading txt as font object
        val font = assetLoader.loadFont(javaClass.getResource("/fxglassets/text/test1.txt"))

        assertThat(font, `is`(notNullValue()))

        // try loading txt as Image
        val image1 = assetLoader.loadImage(javaClass.getResource("/fxglassets/text/test_level.txt"))

        assertThat(image1, `is`(notNullValue()))

        // try loading txt as Texture
        val texture = assetLoader.loadTexture(javaClass.getResource("/fxglassets/text/test_level.txt"), 64.0, 64.0)

        assertThat(texture, `is`(notNullValue()))
    }

    class TestJSONData
    @JvmOverloads constructor(
            var name: String = "",
            var value: Int = 0
    )
}
