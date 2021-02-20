/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.services

import com.almasb.fxgl.app.FXGLApplication
import com.almasb.fxgl.audio.*
import com.almasb.fxgl.core.Inject
import com.almasb.fxgl.core.asset.AssetLoaderService
import com.almasb.fxgl.core.asset.AssetType
import com.almasb.fxgl.core.asset.AssetType.*
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.cutscene.dialogue.DialogueGraph
import com.almasb.fxgl.cutscene.dialogue.DialogueGraphSerializer
import com.almasb.fxgl.cutscene.dialogue.SerializableGraph
import com.almasb.fxgl.cutscene.dialogue.StartNode
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.entity.level.Level
import com.almasb.fxgl.entity.level.LevelLoader
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.scene.CSS
import com.almasb.fxgl.texture.Texture
import com.almasb.fxgl.texture.getDummyImage
import com.almasb.fxgl.ui.FontFactory
import com.almasb.fxgl.ui.UI
import com.almasb.fxgl.ui.UIController
import com.fasterxml.jackson.databind.ObjectMapper
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.image.Image
import javafx.scene.layout.Pane
import javafx.scene.text.Font
import java.io.InputStream
import java.lang.IllegalArgumentException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*


private const val ASSETS_DIR = "/assets/"
private const val TEXTURES_DIR = ASSETS_DIR + "textures/"
private const val SOUNDS_DIR = ASSETS_DIR + "sounds/"
private const val MUSIC_DIR = ASSETS_DIR + "music/"
private const val TEXT_DIR = ASSETS_DIR + "text/"
private const val JSON_DIR = ASSETS_DIR + "json/"
private const val TMX_DIR = ASSETS_DIR + "tmx/"
private const val SCRIPTS_DIR = ASSETS_DIR + "scripts/"
private const val PROPERTIES_DIR = ASSETS_DIR + "properties/"
private const val AI_DIR = ASSETS_DIR + "ai/"
private const val LEVELS_DIR = ASSETS_DIR + "levels/"
private const val DIALOGUES_DIR = ASSETS_DIR + "dialogues/"

private const val UI_DIR = ASSETS_DIR + "ui/"
private const val CSS_DIR = UI_DIR + "css/"
private const val FONTS_DIR = UI_DIR + "fonts/"
private const val CURSORS_DIR = UI_DIR + "cursors/"

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
class FXGLAssetLoaderService : AssetLoaderService() {

    companion object {
        private val NULL_URL = URL("https://github.com/AlmasB/FXGL")

        private val log = Logger.get(FXGLAssetLoaderService::class.java)

        private val assetData = EnumMap<AssetType, AssetLoader<*>>(AssetType::class.java)

        init {
            assetData[IMAGE] = ImageAssetLoader()
            assetData[RESIZABLE_IMAGE] = ResizableImageAssetLoader()
            assetData[TEXT] = TextAssetLoader()
            assetData[DIALOGUE] = DialogueGraphAssetLoader()
            assetData[RESOURCE_BUNDLE] = ResourceBundleAssetLoader()
            assetData[PROPERTY_MAP] = PropertyMapAssetLoader()
            assetData[CSS] = CSSAssetLoader()
            assetData[FONT] = FontAssetLoader()
        }
    }

    @Inject("isDesktop")
    private var isDesktop = true

    @Inject("basePackageForAssets")
    private var basePackageForAssets = ""

    private lateinit var audioService: AudioPlayer

    private val cachedAssets = hashMapOf<String, Any>()

    override fun onInit() {
        assetData[SOUND] = SoundAssetLoader(audioService, isDesktop)
        assetData[MUSIC] = MusicAssetLoader(audioService, isDesktop)
    }

    /**
     * Loads an image with [loadImage] and wraps it with a [Texture].
     */
    fun loadTexture(name: String): Texture {
        return Texture(loadImage(name))
    }

    /**
     * Loads an image with [loadImage] and wraps it with a [Texture].
     */
    fun loadTexture(url: URL): Texture {
        return Texture(loadImage(url))
    }

    /**
     * Loads a resized image with [loadImage] and wraps it with a [Texture].
     */
    fun loadTexture(name: String, width: Double, height: Double): Texture {
        val url = getURL(TEXTURES_DIR + name)

        return Texture(load(RESIZABLE_IMAGE, ResizableImageParams(url, width, height)))
    }

    /**
     * Loads an [Image] with given [name] from /assets/textures/.
     * The image for given [name] will be cached after the first call.
     *
     * Supported image formats are:
     *
     *  * [BMP](http://msdn.microsoft.com/en-us/library/dd183376(v=vs.85).aspx)
     *  * [GIF](http://www.w3.org/Graphics/GIF/spec-gif89a.txt)
     *  * [JPEG](http://www.ijg.org)
     *  * [PNG](http://www.libpng.org/pub/png/spec/)
     *
     * @param name image name without the /assets/textures/, e.g. "player.png"
     * @return a valid image or a dummy image if loading fails
     */
    fun loadImage(name: String): Image {
        return load(IMAGE, name)
    }

    /**
     * Loads an [Image] from given [url].
     * The image for given [url] will be cached after the first call.
     * Supported image formats are:
     *
     *  * [BMP](http://msdn.microsoft.com/en-us/library/dd183376(v=vs.85).aspx)
     *  * [GIF](http://www.w3.org/Graphics/GIF/spec-gif89a.txt)
     *  * [JPEG](http://www.ijg.org)
     *  * [PNG](http://www.libpng.org/pub/png/spec/)
     *
     * @return a valid image or a dummy image if loading fails
     */
    fun loadImage(url: URL): Image {
        return load(IMAGE, url)
    }

    /**
     * Loads a [Sound] with given [name] from /assets/sounds/.
     * The sound for given [name] will be cached after the first call.
     *
     * Supported sound format:
     *  * WAV
     *
     * @param name sound name without the /assets/sounds/, e.g. "explosion.wav"
     * @return a valid sound object or a dummy object if loading fails
     */
    fun loadSound(name: String): Sound {
        return load(SOUND, name)
    }

    /**
     * Loads a [Sound] from given [url].
     * The sound for given [url] will be cached after the first call.
     *
     * Supported sound format:
     *  * WAV
     *
     * @param name sound name without the /assets/sounds/, e.g. "explosion.wav"
     * @return a valid sound object or a dummy object if loading fails
     */
    fun loadSound(url: URL): Sound {
        return load(SOUND, url)
    }

    /**
     * Loads a [Music] with given [name] from /assets/music/.
     * The music for given [name] will be cached after the first call.
     *
     * Supported sound format:
     *  * MP3
     *
     * @param name music name without the /assets/music/, e.g. "bgm.mp3"
     * @return a valid music object or a dummy object if loading fails
     */
    fun loadMusic(name: String): Music {
        return load(MUSIC, name)
    }

    /**
     * Loads a [Music] from given [url].
     * The music for given [url] will be cached after the first call.
     *
     * Supported sound format:
     *  * MP3
     *
     * @param name music name without the /assets/music/, e.g. "bgm.mp3"
     * @return a valid music object or a dummy object if loading fails
     */
    fun loadMusic(url: URL): Music {
        return load(MUSIC, url)
    }

    // TODO: doc
    /**
     * Loads cursor image with given name from /assets/ui/cursors/.
     * Either returns a valid image or throws exception in case of errors.
     *
     * @param name image name without the /assets/ui/cursors/, e.g. "attack_cursor.png"
     * @return cursor image
     * @throws IllegalArgumentException if asset not found or loading error
     */
    @Deprecated("Place cursor under textures/ and use loadImage() instead")
    fun loadCursorImage(name: String): Image {
        val url = getURL(CURSORS_DIR + name)

        return load(IMAGE, url)
    }

    // TODO: doc
    /**
     * Loads resource bundle with given name from "/assets/properties/".
     *
     * Note: for improved mobile support use [loadPropertyMap] instead.
     *
     * @param name must be under "/assets/properties/", e.g. system.properties, game.properties
     * @return resource bundle
     * @throws IllegalArgumentException if asset not found or loading error
     */
    fun loadResourceBundle(name: String): ResourceBundle {
        return load(RESOURCE_BUNDLE, name)
    }

    // TODO: doc
    fun loadResourceBundle(url: URL): ResourceBundle {
        return load(RESOURCE_BUNDLE, url)
    }

    // TODO: doc
    fun loadDialogueGraph(name: String): DialogueGraph {
        val graph = load<SerializableGraph>(DIALOGUE, name)

        return DialogueGraphSerializer.fromSerializable(graph)
    }

    // TODO: doc
    fun loadDialogueGraph(url: URL): DialogueGraph {
        val graph = load<SerializableGraph>(DIALOGUE, url)

        return DialogueGraphSerializer.fromSerializable(graph)
    }

    // TODO: doc
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
    fun loadText(name: String): List<String> {
        return load(TEXT, name)
    }

    // TODO: doc
    fun loadText(url: URL): List<String> {
        return load(TEXT, url)
    }

    // TODO: doc
    // TODO: unlike other types, this is loaded from /assets/ directly...
    // this shouldn't be needed since we have load(URL) ?
    // so use PROPERTIES_DIR?
    /**
     * Loads property map with given name from "/assets/".
     * Example: loadPropertyMap("languages/english.pmap").
     */
    fun loadPropertyMap(name: String): PropertyMap {
        return load(PROPERTY_MAP, name)
    }

    // TODO: doc
    fun loadPropertyMap(url: URL): PropertyMap {
        return load(PROPERTY_MAP, url)
    }

    // TODO: doc
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
        return load(FONT, name)
    }

    // TODO: doc
    fun loadFont(url: URL): FontFactory {
        return load(FONT, url)
    }

    // TODO: doc
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
        return load(CSS, name)
    }

    // TODO: doc
    fun loadCSS(url: URL): CSS {
        return load(CSS, url)
    }

    // TODO: check with new API
    /**
     * @param name level file name in /assets/levels/
     * @param levelLoader level loader to use to load this level
     * @param entityFactory entity factory to use when spawning entities in this level
     */
    fun loadLevel(name: String, levelLoader: LevelLoader): Level {
        return levelLoader.load(getURL(LEVELS_DIR + name), FXGL.getGameWorld())
    }

    // TODO: check with new API
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
     * Load an asset as [assetType] from given [fileName] (relative to its category directory).
     * For example, to load "player.png" from "/assets/textures", the call is
     * load(AssetType.IMAGE, "player.png") with the return type being [Image].
     *
     * Note: specific load methods (e.g. [loadImage]) are preferred over this call.
     */
    override fun <T> load(assetType: AssetType, fileName: String): T {
        return load(assetType, getURL(assetData[assetType]!!.directory + fileName))
    }

    /**
     * Load an asset as [assetType] from given [url].
     * For example,
     * load(AssetType.IMAGE, getClass().getResource("...")) with the return type being [Image].
     *
     * Note: specific load methods (e.g. [loadImage]) are preferred over this call.
     */
    fun <T> load(assetType: AssetType, url: URL): T {
        return load(assetType, LoadParams(url))
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> load(assetType: AssetType, loadParams: LoadParams): T {
        val data = assetData[assetType] as AssetLoader<T>

        if (loadParams.url === NULL_URL) {
            log.warning("Failed to load $assetType")
            return data.getDummy()
        }

        val cacheKey = loadParams.cacheKey

        val asset = cachedAssets[cacheKey]
        if (asset != null) {
            // load from cache
            return data.cast(asset)
        }

        return try {
            // load from file system
            val loaded = data.load(loadParams)
            cachedAssets[cacheKey] = loaded as Any
            loaded
        } catch (e: Exception) {
            log.warning("Failed to load ${loadParams.url}", e)
            data.getDummy()
        }
    }

    // TODO: check with new API
    /**
     * Opens a stream to resource with given name.
     * The caller is responsible for closing the stream.
     * Either returns a valid stream or throws an exception.
     *
     * This is useful for loading resources that do not fall under any asset category.
     * Resource is anything located within the source root / resource root, whether "src/" or "resources/".
     * The resource name must always begin with "/", e.g. "/assets/textures/player.png".
     *
     * @param name resource name
     * @return resource stream
     */
    fun getStream(name: String): InputStream {
        val url = getURL(name)

        if (url === NULL_URL) {
            throw IllegalArgumentException("Asset \"$name\" was not found!")
        }

        return url.openStream()
    }

    /**
     * @param name resource name relative to base package (starts with /assets/)
     * @return a valid URL to resource or [NULL_URL] if URL not found
     */
    private fun getURL(name: String): URL {
        val app = try {
            FXGLApplication.app
        } catch (e: UninitializedPropertyAccessException) {
            null
        }

        var assetPath = name

        if (basePackageForAssets.isNotEmpty()) {
            assetPath = "/${basePackageForAssets.replace('.', '/')}$name"
        }

        // try /assets/ from user module using their class
        val url = app?.javaClass?.getResource(assetPath)
                // try /fxglassets/ from fxgl.all module using this javaclass
                ?: javaClass.getResource("/fxgl${name.substring(1)}")

        if (url == null) {
            log.warning("Asset \"$assetPath\" was not found!")
            return NULL_URL
        }

        return url
    }

    /**
     * Release all cached assets.
     */
    fun clearCache() {
        log.debug("Clearing assets cache")
        cachedAssets.clear()
    }
}

private open class LoadParams(
        val url: URL,
        val cacheKey: String = url.toExternalForm()
)

private class ResizableImageParams(
        url: URL,
        val width: Double,
        val height: Double) : LoadParams(url, url.toExternalForm() + "@" + width + "x" + height)

private sealed class AssetLoader<T>(

        /**
         * Concrete type, e.g. [Image], [Sound].
         */
        val typeClass: Class<T>,

        /**
         * Starts with /assets/...
         */
        val directory: String,
) {
    open fun cast(obj: Any): T = typeClass.cast(obj)

    /**
     * The actual IO / parsing operation.
     */
    open fun load(params: LoadParams): T = load(params.url)

    protected abstract fun load(url: URL): T

    /**
     * @return a dummy for given [typeClass].
     */
    abstract fun getDummy(): T
}

private class ImageAssetLoader : AssetLoader<Image>(
        Image::class.java,
        TEXTURES_DIR
) {
    override fun load(url: URL): Image = url.openStream().use { Image(it) }

    override fun getDummy(): Image = getDummyImage()
}

private class ResizableImageAssetLoader : AssetLoader<Image>(
        Image::class.java,
        TEXTURES_DIR
) {

    override fun load(params: LoadParams): Image {
        val loadParams = params as ResizableImageParams

        return params.url.openStream().use { Image(it, loadParams.width, loadParams.height, false, true) }
    }

    override fun load(url: URL): Image {
        throw UnsupportedOperationException("")
    }

    override fun getDummy(): Image = getDummyImage()
}

private class SoundAssetLoader(val audioService: AudioPlayer, val isDesktop: Boolean) : AssetLoader<Sound>(
        Sound::class.java,
        SOUNDS_DIR
) {
    override fun load(url: URL): Sound = Sound(audioService.loadAudio(AudioType.SOUND, url, isDesktop))

    override fun getDummy(): Sound = Sound(getDummyAudio())
}

private class MusicAssetLoader(val audioService: AudioPlayer, val isDesktop: Boolean) : AssetLoader<Music>(
        Music::class.java,
        MUSIC_DIR
) {
    override fun load(url: URL): Music = Music(audioService.loadAudio(AudioType.MUSIC, url, isDesktop))

    override fun getDummy(): Music = Music(getDummyAudio())
}

private class TextAssetLoader : AssetLoader<List<*>>(
        List::class.java,
        TEXT_DIR
) {
    override fun load(url: URL): List<String> = url.openStream().bufferedReader().readLines()

    override fun getDummy(): List<String> = emptyList()
}

private class DialogueGraphAssetLoader : AssetLoader<SerializableGraph>(
        SerializableGraph::class.java,
        DIALOGUES_DIR
) {
    override fun load(url: URL): SerializableGraph =
            url.openStream().use { ObjectMapper().readValue(it, SerializableGraph::class.java) }

    override fun getDummy(): SerializableGraph {
        val dummyGraph = DialogueGraph()

        // TODO: shouldn't this be handled in dialogue play scene
        // add a start node, so the dialogue can play and not crash at runtime
        dummyGraph.addNode(StartNode("Failed to load dialogue graph"))

        return DialogueGraphSerializer.toSerializable(dummyGraph)
    }
}

private class ResourceBundleAssetLoader : AssetLoader<ResourceBundle>(
        ResourceBundle::class.java,
        PROPERTIES_DIR
) {
    override fun load(url: URL): ResourceBundle =
            url.openStream().use { PropertyResourceBundle(it.reader(StandardCharsets.UTF_8)) }

    override fun getDummy(): ResourceBundle = object : ListResourceBundle() {
        override fun getContents(): Array<Array<Any>> {
            return emptyArray()
        }
    }
}

private class PropertyMapAssetLoader : AssetLoader<PropertyMap>(
        PropertyMap::class.java,
        ASSETS_DIR
) {
    override fun load(url: URL): PropertyMap {
        val lines = url.openStream().bufferedReader().readLines()

        val map = lines
                .filter { it.contains('=') }
                .map {
                    val tokens = it.split("=")
                    tokens[0].trim() to tokens[1].trim()
                }
                .toMap()

        return PropertyMap.fromStringMap(map)
    }

    override fun getDummy(): PropertyMap = PropertyMap()
}

private class CSSAssetLoader : AssetLoader<CSS>(
        com.almasb.fxgl.scene.CSS::class.java,
        CSS_DIR
) {
    override fun load(url: URL): CSS = CSS(url.toExternalForm())

    override fun getDummy(): CSS = CSS("")
}

private class FontAssetLoader : AssetLoader<FontFactory>(
        FontFactory::class.java,
        FONTS_DIR
) {
    override fun load(url: URL): FontFactory {
        return url.openStream().use {
            var font = Font.loadFont(it, 12.0)

            if (font == null) {
                Logger.get<FontAssetLoader>().warning("Font.loadFont($url) returned null")
                font = Font.font(12.0)
            }

            FontFactory(font)
        }
    }

    override fun getDummy(): FontFactory = FontFactory(Font.font(12.0))
}