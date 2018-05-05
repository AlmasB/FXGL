/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.asset

import com.almasb.fxgl.ai.btree.BehaviorTree
import com.almasb.fxgl.ai.btree.utils.BehaviorTreeParser
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.audio.Music
import com.almasb.fxgl.audio.Sound
import com.almasb.fxgl.core.collection.ObjectMap
import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.parser.KVFile
import com.almasb.fxgl.parser.tiled.TMXParser
import com.almasb.fxgl.parser.tiled.TiledMap
import com.almasb.fxgl.scene.CSS
import com.almasb.fxgl.script.Script
import com.almasb.fxgl.script.ScriptFactory
import com.almasb.fxgl.texture.Texture
import com.almasb.fxgl.ui.FontFactory
import com.almasb.fxgl.ui.UI
import com.almasb.fxgl.ui.UIController
import com.fasterxml.jackson.databind.ObjectMapper
import com.gluonhq.charm.down.Services
import com.gluonhq.charm.down.plugins.AudioService
import com.gluonhq.charm.down.plugins.audio.Audio
import com.gluonhq.charm.down.plugins.audio.AudioType
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.text.Font
import java.io.*
import java.net.URL
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * Handles all resource (asset) loading operations.
 * <p>
 * The "assets" directory must be located in source folder ("src" by default).
 * If you are using the Maven directory structure then under "src/main/resources/".
 * <p>
 * Resources (assets) will be searched for in these specified directories:
 * <ul>
 * <li>Texture - /assets/textures/</li>
 * <li>Sound - /assets/sounds/</li>
 * <li>Music - /assets/music/</li>
 * <li>Text (List&lt;String&gt;) - /assets/text/</li>
 * <li>KVFile - /assets/kv/</li>
 * <li>Data - /assets/data/</li>
 * <li>Scripts - /assets/scripts/</li>
 * <li>Behavior Tree - /assets/ai/</li>
 * <li>FXML - /assets/ui/</li>
 * <li>CSS - /assets/ui/css/</li>
 * <li>Font - /assets/ui/fonts/</li>
 * <li>App icons - /assets/ui/icons/</li>
 * <li>Cursors - /assets/ui/cursors/</li>
 * <li>Resource bundles - /assets/properties/</li>
 * </ul>
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
    private val KV_DIR = ASSETS_DIR + "kv/"
    private val JSON_DIR = ASSETS_DIR + "json/"
    private val TMX_DIR = ASSETS_DIR + "tmx/"
    private val BINARY_DIR = ASSETS_DIR + "data/"
    private val SCRIPTS_DIR = ASSETS_DIR + "scripts/"
    private val PROPERTIES_DIR = ASSETS_DIR + "properties/"
    private val AI_DIR = ASSETS_DIR + "ai/"

    private val UI_DIR = ASSETS_DIR + "ui/"
    private val CSS_DIR = UI_DIR + "css/"
    private val FONTS_DIR = UI_DIR + "fonts/"
    private val CURSORS_DIR = UI_DIR + "cursors/"

    private val log = Logger.get(javaClass)

    private val audioService = audioService()

    private fun audioService(): AudioService {
        return if (!FXGL.isIOS())
            Services.get(AudioService::class.java).orElseThrow { RuntimeException("No AudioService present") }
        else
            // ios audio service is not implemented yet, so just mock
            object : AudioService {
                override fun unloadAudio(audio: Audio) {
                }

                override fun loadAudio(audio: AudioType, fileName: String): Audio {
                    return object : Audio(AudioType.MUSIC, fileName) {
                        override fun play() {
                        }

                        override fun setVolume(p0: Double) {
                        }

                        override fun stop() {
                        }

                        override fun pause() {
                        }

                        override fun setOnFinished(p0: Runnable?) {
                        }

                        override fun setLooping(p0: Boolean) {
                        }

                        override fun dispose() {
                        }
                    }
                }
            }
    }

    private val cachedAssets = ObjectMap<String, Any>()

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
            throw loadFailed(name, e)
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
        val asset = getAssetFromCache(TEXTURES_DIR + name)
        if (asset != null) {
            return Texture(Image::class.java.cast(asset))
        }

        try {
            getStream(TEXTURES_DIR + name).use {
                val texture = Texture(Image(it))
                cachedAssets.put(TEXTURES_DIR + name, texture.image)
                return texture
            }
        } catch (e: Exception) {
            throw loadFailed(name, e)
        }
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
            throw loadFailed(name, e)
        }
    }

    /**
     *
     * @param name texture name
     * @param transparency replaces this color with Color.TRANSPARENT
     * @return texture
     */
    fun loadTexture(name: String, transparency: Color): Texture {
        val cacheKey = TEXTURES_DIR + name + "T" + transparency

        val asset = getAssetFromCache(cacheKey)
        if (asset != null) {
            return Texture(Image::class.java.cast(asset))
        }

        try {
            getStream(TEXTURES_DIR + name).use {
                val texture = Texture(Image(it))

                val newTexture = texture.transparentColor(transparency)
                texture.dispose()

                cachedAssets.put(cacheKey, newTexture.image)

                return newTexture
            }
        } catch (e: Exception) {
            throw loadFailed(name, e)
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
            val sound = Sound(audioService.loadAudio(AudioType.SOUND, SOUNDS_DIR + name))
            cachedAssets.put(SOUNDS_DIR + name, sound)
            return sound
        } catch (e: Exception) {
            throw loadFailed(name, e)
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
            val music = Music(audioService.loadAudio(AudioType.MUSIC, MUSIC_DIR + name))
            cachedAssets.put(MUSIC_DIR + name, music)
            return music
        } catch (e: Exception) {
            throw loadFailed(name, e)
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
     * Loads KVFile with given name from /assets/kv/.
     * Either returns a valid KVFile or throws exception in case of errors.
     *
     * @param name KVFile name without the /assets/kv/, .e.g "settings.kv"
     * @return kv file
     * @throws IllegalArgumentException if asset not found or loading error
     */
    fun loadKV(name: String): KVFile {
        return KVFile(readAllLines(KV_DIR + name))
    }

    /**
     * Loads JSON file with given name from /assets/json/.
     * Either returns a valid JSON content or throws exception in case of errors.
     *
     * @param name JSON file name, e.g. level_data.json
     * @return JSON content as String
     * @throws IllegalArgumentException if asset not found or loading error
     */
    fun loadJSON(name: String): List<String> {
        return readAllLines(JSON_DIR + name)
    }

    private val jsonMapper by lazy { ObjectMapper() }

    /**
     * Loads JSON file with given name from /assets/json/ and parses into an object of type T.
     * Either returns a valid parsed object or throws exception in case of errors.
     *
     * The tileset sprite sheets must be located in /assets/textures/
     *
     * @param name JSON file name, e.g. level_data.json
     * @param type object type to parse to
     * @return parsed object
     * @throws IllegalArgumentException if asset not found or loading error
     */
    fun <T : Any> loadJSON(name: String, type: Class<T>): T {
        getStream(JSON_DIR + name).use {
            return jsonMapper.readValue(it, type)
        }
    }

    /**
     * Loads TMX (Tiled Map Editor) file with given name from /assets/tmx/ and parses into
     * a TiledMap.
     * Either returns a valid parsed object or throws exception in case of errors.
     *
     * The tileset sprite sheets must be located in /assets/textures/
     *
     * @param name TMX file name, e.g. level0.tmx
     */
    fun loadTMX(name: String): TiledMap {
        getStream(TMX_DIR + name).use {
            return TMXParser().parse(it)
        }
    }

    /**
     * Loads script with given name from /assets/scripts/ as a single string.
     * Either returns loaded string or throws exception in case of errors.
     *
     * @param name script file without the /assets/scripts/, e.g. "skill_heal.js"
     * @return script as a String
     * @throws IllegalArgumentException if asset not found or loading error
     */
    fun loadScriptRaw(name: String): String {
        return readAllLines(SCRIPTS_DIR + name).joinToString("\n", "", "\n")
    }

    fun loadScript(name: String): Script {
        val code = loadScriptRaw(name)
        return ScriptFactory.fromCode(code)
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
                val bundle = PropertyResourceBundle(InputStreamReader(it, StandardCharsets.UTF_8))
                cachedAssets.put(PROPERTIES_DIR + name, bundle)
                return bundle
            }
        } catch (e: Exception) {
            throw loadFailed(name, e)
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
            throw loadFailed(name, e)
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
            throw loadFailed(name, e)
        }
    }

    /**
     * Loads a CSS file from /assets/ui/css/.
     * Can be applied by calling object.getStyleSheets().add(css.getExternalForm()).
     * Either returns ready CSS or throws exception in case of errors.

     * @param name CSS file name without the /assets/ui/css/, e.g. "ui_button.css"
     * *
     * @return css
     * *
     * @throws IllegalArgumentException if asset not found or loading error
     */
    fun loadCSS(name: String): CSS {
        try {
            return CSS(getURL(CSS_DIR + name).toExternalForm())
        } catch (e: Exception) {
            throw loadFailed(name, e)
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
                var font: Font? = Font.loadFont(it, 12.0)
                if (font == null)
                    font = Font.font(12.0)
                val fontFactory = FontFactory(font!!)
                cachedAssets.put(FONTS_DIR + name, fontFactory)
                return fontFactory
            }
        } catch (e: Exception) {
            throw loadFailed(name, e)
        }
    }

    /**
     * Loads a behavior tree from /assets/ai/.
     * Either returns a valid behavior tree or throws an exception in case of errors.
     *
     * @param name tree name without the /assets/ai/, e.g. "patrol.tree"
     * @param  tree type
     * @return loaded and parsed behavior tree
     * @throws IllegalArgumentException if asset not found or loading error
     */
    fun <T> loadBehaviorTree(name: String): BehaviorTree<T> {
        try {
            getStream(AI_DIR + name).use { return BehaviorTreeParser<T>().parse(it, null) }
        } catch (e: Exception) {
            throw loadFailed(name, e)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> loadDataInternal(name: String): T {
        try {
            ObjectInputStream(getStream(BINARY_DIR + name)).use { return it.readObject() as T }
        } catch (e: Exception) {
            throw loadFailed(name, e)
        }
    }

    /**
     * Returns a valid URL to resource or throws [IllegalArgumentException].
     *
     * @param name resource name
     * @return URL to resource
     */
    private fun getURL(name: String): URL {
        log.debug("Loading from disk: " + name)

        return javaClass.getResource(name) ?: throw IllegalArgumentException("Asset \"$name\" was not found!")
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
     * Load an asset from cache.
     *
     * @param name asset name
     * @return asset object or null if not found
     */
    private fun getAssetFromCache(name: String): Any? {
        val asset = cachedAssets.get(name)
        if (asset != null) {
            log.debug("Loading from cache: $name")
            return asset
        } else {
            return null
        }
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
            BufferedReader(InputStreamReader(getStream(name))).use {
                val result = ArrayList<String>()

                while (true) {
                    val line = it.readLine() ?: break
                    result.add(line)
                }
                return result
            }
        } catch (e: Exception) {
            throw loadFailed(name, e)
        }
    }

    /**
     * Pre-loads all textures / sounds / music / text / fonts and binary data
     * from their respective folders.
     */
    fun cache() {
        log.debug("Caching assets")

        loadFileNames(TEXTURES_DIR).forEach { loadTexture(it) }
        loadFileNames(SOUNDS_DIR).forEach { loadSound(it) }
        loadFileNames(MUSIC_DIR).forEach { loadMusic(it) }
        loadFileNames(TEXT_DIR).forEach { loadText(it) }
        loadFileNames(FONTS_DIR).forEach { loadFont(it) }
        loadFileNames(BINARY_DIR).forEach { loadDataInternal(it) }

        log.debug("Caching complete. Size: ${cachedAssets.size()}")
    }

    /**
     * Release all cached assets.
     */
    fun clearCache() {
        log.debug("Clearing assets cache")
        cachedAssets.clear()
    }

    /**
     * Loads file names from a directory.
     * Note: directory name must be in the format "/assets/...".
     * Returned file names are relativized to the given directory name.
     *
     * @param directory name of directory
     * @return list of file names
     * @throws IllegalArgumentException if directory does not start with "/assets/" or was not found
     */
    fun loadFileNames(directory: String): List<String> {
        if (!directory.startsWith(ASSETS_DIR))
            throw IllegalArgumentException("Directory must start with: $ASSETS_DIR Provided: $directory")

        try {
            val url = javaClass.getResource(directory)
            if (url != null) {
                if (url.toString().startsWith("jar"))
                    return loadFileNamesJar(directory.substring(1))

                val dir = Paths.get(url.toURI())

                if (Files.exists(dir)) {
                    return Files.walk(dir)
                            .filter { Files.isRegularFile(it) }
                            .map { dir.relativize(it).toString().replace("\\", "/") }
                            .collect(Collectors.toList<String>())
                }
            }

            return loadFileNamesJar(directory.substring(1))
        } catch (e: Exception) {
            throw loadFailed(directory, e)
        }
    }

    /**
     * Loads file names from a directory when running within a jar.
     * If it contains other folders they'll be searched too.
     *
     * @param folderName folder files of which need to be retrieved
     * @return list of file names
     */
    private fun loadFileNamesJar(folderName: String): List<String> {
        val fileNames = ArrayList<String>()
        val src = AssetLoader::class.java.protectionDomain.codeSource
        if (src != null) {
            val jar = src.location
            try {
                jar.openStream().use {
                    ZipInputStream(it).use { zip ->
                        var ze: ZipEntry
                        while (true) {
                            ze = zip.nextEntry ?: break

                            val entryName = ze.name
                            if (entryName.startsWith(folderName)) {
                                if (entryName.endsWith("/"))
                                    continue
                                fileNames.add(entryName.substring(entryName.indexOf(folderName) + folderName.length))
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                log.warning("Failed to load file names from jar - " + e)
            }

        } else {
            log.warning("Failed to load file names from jar - No code source")
        }

        return fileNames
    }

    /**
     * Constructs new IllegalArgumentException with "load failed" message
     * and with relevant information about the asset.
     *
     * @param assetName name of the asset load of which failed
     * @param error the error that occurred
     * @return instance of IAE to be thrown
     */
    private fun loadFailed(assetName: String, error: Throwable): IllegalArgumentException {
        log.fatal("Loading failed for asset: " + assetName + ". Cause: " + error.message)
        return IllegalArgumentException("Failed to load asset: " + assetName + ". Cause: " + error.message)
    }
}