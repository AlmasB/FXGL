/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.audio.AudioType
import com.almasb.fxgl.audio.Music
import com.almasb.fxgl.audio.Sound
import com.almasb.fxgl.audio.getDummyAudio
import com.almasb.fxgl.audio.impl.DesktopAudioService
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.entity.level.Level
import com.almasb.fxgl.entity.level.LevelLoader
import com.almasb.fxgl.texture.Texture
import com.almasb.fxgl.texture.getDummyImage
import com.almasb.fxgl.ui.FontFactory
import com.almasb.fxgl.ui.UI
import com.almasb.fxgl.ui.UIController
import com.almasb.sslogger.Logger
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.image.Image
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * Handles all resource (asset) loading operations.
 * 
 * The "assets" directory must be located in "src/main/resources/" (using the Maven directory structure).
 * Alternatively, (not recommended) source folder ("src" by default).
 * 
 * Resources (assets) will be searched for in these specified directories:
 * 
 * Texture / images - /assets/textures/
 * Sound - /assets/sounds/
 * Music - /assets/music/
 * Text (List of String) - /assets/text/
 * Scripts - /assets/scripts/
 * Behavior Tree - /assets/ai/
 * FXML - /assets/ui/
 * CSS - /assets/ui/css/
 * Font - /assets/ui/fonts/
 * Cursors - /assets/ui/cursors/
 * Resource bundles - /assets/properties/
 *
 * If you need to access the "raw" JavaFX objects (e.g. Image), you can use
 * {@link AssetLoader#getStream(String)} to obtain an InputStream and then
 * parse into whatever resource you need.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class AssetLoader {

    private val ASSETS_DIR = "/assets/"
    private val TEXTURES_DIR = ASSETS_DIR + "textures/"
    private val SOUNDS_DIR = ASSETS_DIR + "sounds/"
    private val MUSIC_DIR = ASSETS_DIR + "music/"
    private val TEXT_DIR = ASSETS_DIR + "text/"
    private val JSON_DIR = ASSETS_DIR + "json/"
    private val TMX_DIR = ASSETS_DIR + "tmx/"
    private val SCRIPTS_DIR = ASSETS_DIR + "scripts/"
    private val PROPERTIES_DIR = ASSETS_DIR + "properties/"
    private val AI_DIR = ASSETS_DIR + "ai/"
    private val LEVELS_DIR = ASSETS_DIR + "levels/"

    private val UI_DIR = ASSETS_DIR + "ui/"
    private val CSS_DIR = UI_DIR + "css/"
    private val FONTS_DIR = UI_DIR + "fonts/"
    private val CURSORS_DIR = UI_DIR + "cursors/"

    private val log = Logger.get(javaClass)

    private val audioService = DesktopAudioService()

    private val cachedAssets = hashMapOf<String, Any>()

    /**
     * Loads texture as [Image] with given name from /assets/textures/.
     * Either returns a valid image or throws an exception in case of errors.
     *
     * Supported image formats are:
     *
     *  * [BMP](http://msdn.microsoft.com/en-us/library/dd183376(v=vs.85).aspx)
     *  * [GIF](http://www.w3.org/Graphics/GIF/spec-gif89a.txt)
     *  * [JPEG](http://www.ijg.org)
     *  * [PNG](http://www.libpng.org/pub/png/spec/)
     *
     * @param name texture name without the /assets/textures/, e.g. "player.png"
     * @return image
     * @throws IllegalArgumentException if asset not found or loading error
     */
    fun loadImage(name: String): Image {
        val asset = getAssetFromCache(TEXTURES_DIR + name)
        if (asset != null) {
            return Image::class.java.cast(asset)
        }

        try {
            getStream(TEXTURES_DIR + name).use {
                val image = Image(it)
                cachedAssets.put(TEXTURES_DIR + name, image)
                return image
            }
        } catch (e: Exception) {
            log.warning("Failed to load texture $name", e)
            return getDummyImage()
        }
    }

    /**
     * Loads texture with given name from /assets/textures/.
     * Either returns a valid texture or throws an exception in case of errors.
     *
     * Supported image formats are:
     *
     *  * [BMP](http://msdn.microsoft.com/en-us/library/dd183376(v=vs.85).aspx)
     *  * [GIF](http://www.w3.org/Graphics/GIF/spec-gif89a.txt)
     *  * [JPEG](http://www.ijg.org)
     *  * [PNG](http://www.libpng.org/pub/png/spec/)
     *
     * @param name texture name without the /assets/textures/, e.g. "player.png"
     * @return texture
     * @throws IllegalArgumentException if asset not found or loading error
     */
    fun loadTexture(name: String): Texture {
        return Texture(loadImage(name))
    }

    /**
     * Loads texture with given name from /assets/textures/.
     * Then resizes it to given width and height without preserving aspect ratio.
     * Either returns a valid texture or throws an exception in case of errors.
     *
     * Supported image formats are:
     *
     *  * [BMP](http://msdn.microsoft.com/en-us/library/dd183376(v=vs.85).aspx)
     *  * [GIF](http://www.w3.org/Graphics/GIF/spec-gif89a.txt)
     *  * [JPEG](http://www.ijg.org)
     *  * [PNG](http://www.libpng.org/pub/png/spec/)
     *
     * @param name texture name without the /assets/textures/, e.g. "player.png"
     * @param width requested width
     * @param height requested height
     * @return texture
     * @throws IllegalArgumentException if asset not found or loading error
     */
    fun loadTexture(name: String, width: Double, height: Double): Texture {
        val cacheKey = TEXTURES_DIR + name + "@" + width + "x" + height

        val asset = getAssetFromCache(cacheKey)
        if (asset != null) {
            return Texture(Image::class.java.cast(asset))
        }

        try {
            getStream(TEXTURES_DIR + name).use {
                val texture = Texture(Image(it, width, height, false, true))
                cachedAssets.put(cacheKey, texture.image)
                return texture
            }
        } catch (e: Exception) {
            log.warning("Failed to load texture $name", e)
            return Texture(getDummyImage())
        }
    }

    /**
     * Loads sound with given name from /assets/sounds/.
     * Either returns a valid sound or throws an exception in case of errors.
     *
     * Supported sound format:
     *  * WAV
     *
     * @param name sound name without the /assets/sounds/, e.g. "explosion.wav"
     * @return sound
     * @throws IllegalArgumentException if asset not found or loading error
     */
    fun loadSound(name: String): Sound {
        val asset = getAssetFromCache(SOUNDS_DIR + name)
        if (asset != null) {
            return Sound::class.java.cast(asset)
        }

        try {
            val sound = Sound(audioService.loadAudio(AudioType.SOUND, getURL(SOUNDS_DIR + name)))
            cachedAssets.put(SOUNDS_DIR + name, sound)
            return sound
        } catch (e: Exception) {
            log.warning("Failed to load sound $name", e)
            return Sound(getDummyAudio())
        }
    }

    /**
     * Loads sound with given name from /assets/music/.
     * Either returns a valid sound or throws an exception in case of errors.
     *
     * Supported music format:
     *  * MP3
     *
     * @param name music name without the /assets/music/, e.g. "background_music.mp3"
     * @return music
     * @throws IllegalArgumentException if asset not found or loading error
     */
    fun loadMusic(name: String): Music {
        val asset = getAssetFromCache(MUSIC_DIR + name)
        if (asset != null) {
            return Music::class.java.cast(asset)
        }

        try {
            val music = Music(audioService.loadAudio(AudioType.MUSIC, getURL(MUSIC_DIR + name)))
            cachedAssets.put(MUSIC_DIR + name, music)
            return music
        } catch (e: Exception) {
            log.warning("Failed to load music $name", e)
            return Music(getDummyAudio())
        }
    }

    /**
     * Loads text file with given name from /assets/text/
     * into List where each element represents a line
     * in the file. Either returns a valid list with lines read from the file
     * or throws an exception in case of errors.
     *
     * @param name text file name without the /assets/text/, e.g. "level_0.txt"
     * @return list of lines from file
     * @throws IllegalArgumentException if asset not found or loading error
     */
    @Suppress("UNCHECKED_CAST")
    fun loadText(name: String): List<String> {
        val asset = getAssetFromCache(TEXT_DIR + name)
        if (asset != null) {
            return asset as List<String>
        }

        val text = readAllLines(TEXT_DIR + name)
        cachedAssets.put(TEXT_DIR + name, text)
        return text
    }

    /**
     * @param name level file name in /assets/levels/
     * @param levelLoader level loader to use to load this level
     * @param entityFactory entity factory to use when spawning entities in this level
     */
    fun loadLevel(name: String, levelLoader: LevelLoader): Level {
        return levelLoader.load(getURL(LEVELS_DIR + name), FXGL.getGameWorld())
    }

    /**
     * Loads resource bundle with given name from "/assets/properties/".
     *
     * @param name must be under "/assets/properties/", e.g. system.properties, game.properties
     * @return resource bundle
     * @throws IllegalArgumentException if asset not found or loading error
     */
    fun loadResourceBundle(name: String): ResourceBundle {
        val asset = getAssetFromCache(PROPERTIES_DIR + name)
        if (asset != null) {
            return asset as ResourceBundle
        }

        try {
            getStream(PROPERTIES_DIR + name).use {
                val bundle = PropertyResourceBundle(it.reader(StandardCharsets.UTF_8))
                cachedAssets.put(PROPERTIES_DIR + name, bundle)
                return bundle
            }
        } catch (e: Exception) {
            log.warning("Failed to load resource bundle $name", e)
            return object : ListResourceBundle() {
                override fun getContents(): Array<Array<Any>> {
                    return emptyArray()
                }
            }
        }
    }

    /**
     * Loads cursor image with given name from /assets/ui/cursors/.
     * Either returns a valid image or throws exception in case of errors.
     *
     * @param name image name without the /assets/ui/cursors/, e.g. "attack_cursor.png"
     * @return cursor image
     * @throws IllegalArgumentException if asset not found or loading error
     */
    fun loadCursorImage(name: String): Image {
        try {
            getStream(CURSORS_DIR + name).use { return Image(it) }
        } catch (e: Exception) {
            log.warning("Failed to load cursor image $name", e)
            return getDummyImage()
        }
    }

    /**
     * Loads an FXML (.fxml) file from /assets/ui/.
     * Either returns a valid parsed UI or throws an exception in case of errors.
     *
     * @param name FXML file name
     * @param controller the controller object
     * @return a UI object parsed from .fxml
     * @throws IllegalArgumentException if asset not found or loading/parsing error
     */
    fun loadUI(name: String, controller: UIController): UI {
        try {
            getStream(UI_DIR + name).use {
                val loader = FXMLLoader()
                loader.setController(controller)
                val root = loader.load<Parent>(it)
                controller.init()
                return UI(root, controller)
            }
        } catch (e: Exception) {
            log.warning("Failed to load FXML $name", e)
            log.warning("Failed to load UI, so controller.init() will not be called")
            return UI(Pane(), controller)
        }
    }

    /**
     * Loads a CSS file from /assets/ui/css/.
     * Can be applied by calling object.getStyleSheets().add(css.getExternalForm()).
     * Either returns ready CSS or throws exception in case of errors.
     * 
     * @param name CSS file name without the /assets/ui/css/, e.g. "ui_button.css"
     * @return css
     * @throws IllegalArgumentException if asset not found or loading error
     */
    fun loadCSS(name: String): CSS {
        try {
            return CSS(getURL(CSS_DIR + name).toExternalForm())
        } catch (e: Exception) {
            log.warning("Failed to load css $name", e)
            return CSS("")
        }
    }

    /**
     * Loads a native JavaFX font with given name from /assets/ui/fonts/
     * wrapped in a FontFactory, which later can be used to produce fonts
     * with different sizes without accessing the font file.
     * Either returns a valid font factory or throws exception in case of errors.
     *
     * Supported font formats are:
     *
     *  * TTF
     *  * OTF
     *
     * @param name font file name without the /assets/ui/fonts/, e.g. "quest_font.ttf"
     * @return font factory
     * @throws IllegalArgumentException if asset not found or loading error
     */
    fun loadFont(name: String): FontFactory {
        val asset = getAssetFromCache(FONTS_DIR + name)
        if (asset != null) {
            return FontFactory::class.java.cast(asset)
        }

        try {
            getStream(FONTS_DIR + name).use {
                val font = Font.loadFont(it, 12.0) ?: throw IllegalArgumentException("Font.loadFont($name) returned null")

                val fontFactory = FontFactory(font)
                cachedAssets[FONTS_DIR + name] = fontFactory
                return fontFactory
            }
        } catch (e: Exception) {
            log.warning("Failed to load font $name", e)
            return FontFactory(Font.font(12.0))
        }
    }

    /**
     * Opens a stream to resource with given name.
     * The caller is responsible for closing the stream.
     * Either returns a valid stream or throws an exception.

     * This is useful for loading resources that do not fall under any asset category.
     * Resource is anything located within the source root / resource root, whether "src/" or "resources/".
     * The resource name must always begin with "/", e.g. "/assets/textures/player.png".

     * @param name resource name
     * @return resource stream
     * @throws IllegalArgumentException if any error occurs or stream is null
     */
    fun getStream(name: String): InputStream {
        try {
            return getURL(name).openStream() ?: throw IOException("Input stream to \"$name\" is null!")
        } catch (e: IOException) {
            throw IllegalArgumentException("Failed to obtain input stream to URL: $e")
        }
    }

    /**
     * Returns a valid URL to resource or throws [IllegalArgumentException].
     *
     * @param name resource name
     * @return URL to resource
     */
    private fun getURL(name: String): URL {
        log.debug("Loading from file system: $name")

        // try /assets/ from user module using their class
        return GameApplication.FXGLApplication.app?.javaClass?.getResource(name)
                // try /fxglassets/ from fxgl.all module using this javaclass
                ?: javaClass.getResource("/fxgl${name.substring(1)}")
                ?: throw IllegalArgumentException("Asset \"$name\" was not found!")
    }

    /**
     * Load an asset from cache.
     *
     * @param name asset name
     * @return asset object or null if not found
     */
    private fun getAssetFromCache(name: String): Any? {
        val asset = cachedAssets[name]
        if (asset != null) {
            log.debug("Loading from cache: $name")
            return asset
        }

        return null
    }

    /**
     * Read all lines from a file. Bytes from the file are decoded into characters
     * using the [UTF-8][java.nio.charset.StandardCharsets.UTF_8] [charset][java.nio.charset.Charset].
     *
     * @param name resource name
     * @return the lines from the file as a `List`
     */
    private fun readAllLines(name: String): List<String> {
        try {
            return getStream(name).bufferedReader().readLines()
        } catch (e: Exception) {
            log.warning("Failed to load plain text file $name", e)
            return emptyList()
        }
    }

    /**
     * Release all cached assets.
     */
    fun clearCache() {
        log.debug("Clearing assets cache")
        cachedAssets.clear()
    }
}