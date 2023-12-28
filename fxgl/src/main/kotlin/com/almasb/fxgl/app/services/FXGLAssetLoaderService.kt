/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.services

import com.almasb.fxgl.audio.*
import com.almasb.fxgl.core.Inject
import com.almasb.fxgl.core.asset.AssetLoaderService
import com.almasb.fxgl.core.asset.AssetType
import com.almasb.fxgl.core.asset.AssetType.*
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.cutscene.Cutscene
import com.almasb.fxgl.cutscene.dialogue.DialogueGraph
import com.almasb.fxgl.cutscene.dialogue.DialogueGraphSerializer
import com.almasb.fxgl.cutscene.dialogue.SerializableGraph
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.entity.level.Level
import com.almasb.fxgl.entity.level.LevelLoader
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.scene.CSS
import com.almasb.fxgl.scene3d.Model3D
import com.almasb.fxgl.scene3d.obj.ObjModelLoader
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
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.media.MediaView
import javafx.scene.text.Font
import java.io.InputStream
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*

// Directories that are used for specific assets
private const val ASSETS_DIR = "/assets/"
private const val TEXTURES_DIR = ASSETS_DIR + "textures/"
private const val SOUNDS_DIR = ASSETS_DIR + "sounds/"
private const val MUSIC_DIR = ASSETS_DIR + "music/"
private const val TEXT_DIR = ASSETS_DIR + "text/"
private const val PROPERTIES_DIR = ASSETS_DIR + "properties/"
private const val LEVELS_DIR = ASSETS_DIR + "levels/"
private const val DIALOGUES_DIR = ASSETS_DIR + "dialogues/"
private const val MODELS_DIR = ASSETS_DIR + "models/"
private const val VIDEOS_DIR = ASSETS_DIR + "videos/"

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
 * FXML - /assets/ui/
 * CSS - /assets/ui/css/
 * Font - /assets/ui/fonts/
 * Cursors - /assets/ui/cursors/
 * Resource bundles - /assets/properties/
 *
 * If you need to access the "raw" JavaFX objects (e.g. Image), you can use [getStream]
 * to obtain an InputStream and then parse into whatever resource you need.
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
            assetData[UI] = UIAssetLoader()
            assetData[CSS] = CSSAssetLoader()
            assetData[FONT] = FontAssetLoader()
            assetData[MODEL3D] = Model3DAssetLoader()
            assetData[VIDEO] = VideoAssetLoader()
        }
    }

    @Inject("isMobile")
    private var isMobile = false

    @Inject("userAppClass")
    private lateinit var userAppClass: Class<*>

    private lateinit var audioService: AudioPlayer

    private val cachedAssets = hashMapOf<String, Any>()

    override fun onInit() {
        assetData[SOUND] = SoundAssetLoader(audioService, isMobile)
        assetData[MUSIC] = MusicAssetLoader(audioService, isMobile)

        log.debug("User app class for loading assets: $userAppClass")
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
     * Loads a resized image with [loadImage] and wraps it with a [Texture].
     */
    fun loadTexture(url: URL, width: Double, height: Double): Texture {
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

    /**
     * Loads resource bundle with given [name] from "/assets/properties/".
     *
     * Note: for improved mobile support use [loadPropertyMap] instead.
     *
     * @param name must be under "/assets/properties/", e.g. system.properties, game.properties
     * @return resource bundle
     */
    fun loadResourceBundle(name: String): ResourceBundle {
        return load(RESOURCE_BUNDLE, name)
    }

    /**
     * Loads resource bundle from given [url].
     *
     * Note: for improved mobile support use [loadPropertyMap] instead.
     *
     * @return resource bundle
     */
    fun loadResourceBundle(url: URL): ResourceBundle {
        return load(RESOURCE_BUNDLE, url)
    }

    /**
     * Load [DialogueGraph] with given [name] from "/assets/dialogues".
     *
     * @param name must be under "/assets/dialogues", e.g. npc_dialogue.json
     * @return dialogue graph or empty graph if errors
     */
    fun loadDialogueGraph(name: String): DialogueGraph {
        val graph = load<SerializableGraph>(DIALOGUE, name)

        return DialogueGraphSerializer.fromSerializable(graph)
    }

    /**
     * Load [DialogueGraph] from given [url].
     *
     * @return dialogue graph or empty graph if errors
     */
    fun loadDialogueGraph(url: URL): DialogueGraph {
        val graph = load<SerializableGraph>(DIALOGUE, url)

        return DialogueGraphSerializer.fromSerializable(graph)
    }

    /**
     * @return [Cutscene] with given [name] from "/assets/text", e.g. cutscene.txt
     */
    fun loadCutscene(name: String): Cutscene {
        return Cutscene(loadText(name))
    }

    /**
     * @return [Cutscene] with given [url]
     */
    fun loadCutscene(url: URL): Cutscene {
        return Cutscene(loadText(url))
    }

    /**
     * Loads text file with given [name] from /assets/text/
     * into List<String> where each element represents a line in the file.
     *
     * @param name text file name without the /assets/text/, e.g. "level_0.txt"
     * @return list of lines from file or empty list if errors
     */
    fun loadText(name: String): List<String> {
        return load(TEXT, name)
    }

    /**
     * Loads text file from given [url]
     * into List<String> where each element represents a line in the file.
     *
     * @return list of lines from file or empty list if errors
     */
    fun loadText(url: URL): List<String> {
        return load(TEXT, url)
    }

    /**
     * Loads .json file with [name] from "/assets/" as [type].
     * The object is not cached.
     *
     * @return parsed object with [type] or Optional.empty() if errors
     */
    fun <T> loadJSON(name: String, type: Class<T>): Optional<T & Any> {
        return loadJSON(getURL(ASSETS_DIR + name), type)
    }

    /**
     * Loads .json file from [url] as [type].
     * The object is not cached.
     *
     * @return parsed object with [type] or Optional.empty() if errors
     */
    fun <T> loadJSON(url: URL, type: Class<T>): Optional<T & Any> {
        if (url === NULL_URL) {
            log.warning("Failed to load JSON: URL is not valid")
            return Optional.empty()
        }

        // impl of json loading is direct (doesn't use our unified loading)
        // to avoid potential cast issues
        log.debug("Loading JSON from: $url")

        try {
            url.openStream().use {
                val obj = ObjectMapper().readValue(it, type)
                return Optional.ofNullable(obj)
            }
        } catch (e: Exception) {
            log.warning("Loading failed: $url", e)
            return Optional.empty()
        }
    }

    /**
     * Loads property map with given [name] from "/assets/".
     * Example: loadPropertyMap("languages/english.pmap").
     *
     * @return property map or empty one if errors
     */
    fun loadPropertyMap(name: String): PropertyMap {
        return load(PROPERTY_MAP, name)
    }

    /**
     * Load a property map from given [URL].
     *
     * @return property map or empty one if errors
     */
    fun loadPropertyMap(url: URL): PropertyMap {
        return load(PROPERTY_MAP, url)
    }

    /**
     * Loads a native JavaFX font with given [name] from /assets/ui/fonts/
     * wrapped in a FontFactory, which later can be used to produce fonts
     * with different sizes without accessing the font file.
     *
     * Supported font formats are:
     *
     *  * TTF
     *  * OTF
     *
     * @param name font file name without the /assets/ui/fonts/, e.g. "quest_font.ttf"
     * @return font factory or default font factory if errors
     */
    fun loadFont(name: String): FontFactory {
        return load(FONT, name)
    }

    /**
     * Loads a native JavaFX font from given [url]
     * wrapped in a FontFactory, which later can be used to produce fonts
     * with different sizes without accessing the font file.
     *
     * Supported font formats are:
     *
     *  * TTF
     *  * OTF
     *
     * @return font factory or default font factory if errors
     */
    fun loadFont(url: URL): FontFactory {
        return load(FONT, url)
    }

    /**
     * Loads a CSS file with given [name] from /assets/ui/css/.
     * Can be applied by calling object.getStyleSheets().add(css.getExternalForm()).
     *
     * @param name CSS file name without the /assets/ui/css/, e.g. "ui_button.css"
     * @return css or empty css if errors
     */
    fun loadCSS(name: String): CSS {
        return load(CSS, name)
    }

    /**
     * Loads a CSS file from [url].
     * Can be applied by calling object.getStyleSheets().add(css.getExternalForm()).
     *
     * @return css or empty css if errors
     */
    fun loadCSS(url: URL): CSS {
        return load(CSS, url)
    }

    /**
     * @param name level file name in /assets/levels/
     * @param levelLoader level loader to use to load this level
     * @return level or throws [IllegalArgumentException] if asset is not found
     */
    fun loadLevel(name: String, levelLoader: LevelLoader): Level {
        val url = getURL(LEVELS_DIR + name)

        if (url === NULL_URL) {
            throw IllegalArgumentException("Loading level failed. Asset not found: $name")
        }

        return levelLoader.load(url, FXGL.getGameWorld())
    }

    /**
     * Loads an FXML (.fxml) file with given [name] from /assets/ui/.
     * UI objects are not cached.
     *
     * @param name FXML file name
     * @param controller the controller object
     * @return a UI object parsed from .fxml or empty UI if errors
     */
    fun loadUI(name: String, controller: UIController): UI {
        val url = getURL(UI_DIR + name)

        return load(UI, UIParams(url, controller))
    }

    /**
     * Loads an FXML (.fxml) file from given [url].
     * UI objects are not cached.
     *
     * @param controller the controller object
     * @return a UI object parsed from .fxml or empty UI if errors
     */
    fun loadUI(url: URL, controller: UIController): UI {
        return load(UI, UIParams(url, controller))
    }

    /**
     * Loads a 3D model from file with given [name] from /assets/models/.
     */
    fun loadModel3D(name: String): Model3D {
        return load(MODEL3D, name)
    }

    /**
     * Loads a 3D model from given [url].
     */
    fun loadModel3D(url: URL): Model3D {
        return load(MODEL3D, url)
    }

    /**
     * Loads a video from file with given [name] from /assets/videos/.
     */
    fun loadVideo(name: String): MediaView {
        return load(VIDEO, name)
    }

    /**
     * Loads a video file from given [url].
     */
    fun loadVideo(url: URL): MediaView {
        return load(VIDEO, url)
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
    override fun <T> load(assetType: AssetType, url: URL): T {
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
            log.debug("Loading from file system: ${loadParams.url}")

            val loaded = data.load(loadParams)

            if (loadParams.isCacheEnabled) {
                cachedAssets[cacheKey] = loaded as Any
            }

            data.cast(loaded as Any)
        } catch (e: Exception) {
            log.warning("Failed to load ${loadParams.url}", e)
            data.getDummy()
        }
    }

    /**
     * Opens a stream to resource with given [name].
     * The caller is responsible for closing the stream.
     * Either returns a valid stream or throws an exception.
     *
     * This is useful for loading resources that do not fall under any asset category.
     * Resource is anything located within the source root or resource root, e.g. "src/" or "resources/".
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
    override fun getURL(name: String): URL {
        // 1. try /assets/ from user module using their app class
        // 2. try /fxglassets/ from fxgl.all module using this javaclass

        val url = userAppClass.getResource(name)
                ?: javaClass.getResource("/fxgl${name.substring(1)}")

        if (url == null) {
            log.warning("Asset \"$name\" was not found!")
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
        val cacheKey: String = url.toExternalForm(),
        val isCacheEnabled: Boolean = true
)

private class ResizableImageParams(
        url: URL,
        val width: Double,
        val height: Double) : LoadParams(url, url.toExternalForm() + "@" + width + "x" + height)

private class UIParams(
        url: URL,
        val controller: UIController) : LoadParams(url, isCacheEnabled = false)

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

    @Throws(java.lang.Exception::class)
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
    override fun load(url: URL): Image {
        val image = Image(url.toExternalForm())

        if (image.isError) {
            throw image.exception
        }

        return image
    }

    override fun getDummy(): Image = getDummyImage()
}

private class ResizableImageAssetLoader : AssetLoader<Image>(
        Image::class.java,
        TEXTURES_DIR
) {

    override fun load(params: LoadParams): Image {
        val loadParams = params as ResizableImageParams

        val image = Image(params.url.toExternalForm(), loadParams.width, loadParams.height, false, true)

        if (image.isError) {
            throw image.exception
        }

        return image
    }

    override fun load(url: URL): Image {
        throw UnsupportedOperationException("")
    }

    override fun getDummy(): Image = getDummyImage()
}

private class SoundAssetLoader(val audioService: AudioPlayer, val isMobile: Boolean) : AssetLoader<Sound>(
        Sound::class.java,
        SOUNDS_DIR
) {
    override fun load(url: URL): Sound = Sound(audioService.loadAudio(AudioType.SOUND, url, isMobile))

    override fun getDummy(): Sound = Sound(getDummyAudio())
}

private class MusicAssetLoader(val audioService: AudioPlayer, val isMobile: Boolean) : AssetLoader<Music>(
        Music::class.java,
        MUSIC_DIR
) {
    override fun load(url: URL): Music = Music(audioService.loadAudio(AudioType.MUSIC, url, isMobile))

    override fun getDummy(): Music = Music(getDummyAudio())
}

private class TextAssetLoader : AssetLoader<List<*>>(
        List::class.java,
        TEXT_DIR
) {
    override fun cast(obj: Any): List<String> {
        val list = obj as List<String>

        return list.toList()
    }

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
        return DialogueGraphSerializer.toSerializable(DialogueGraph())
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
    override fun cast(obj: Any): PropertyMap {
        val map = obj as PropertyMap

        return map.copy()
    }

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

private class UIAssetLoader : AssetLoader<UI>(
        com.almasb.fxgl.ui.UI::class.java,
        UI_DIR
) {
    override fun load(params: LoadParams): UI {
        val controller = (params as UIParams).controller

        params.url.openStream().use {
            val loader = FXMLLoader()
            loader.setController(controller)
            val root = loader.load<Parent>(it)
            controller.init()
            return UI(root, controller)
        }
    }

    override fun load(url: URL): UI {
        throw UnsupportedOperationException("")
    }

    override fun getDummy(): UI {
        return UI(Pane(), object : UIController {
            override fun init() {
            }
        })
    }
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

private class Model3DAssetLoader : AssetLoader<Model3D>(
        Model3D::class.java,
        MODELS_DIR
) {

    override fun cast(obj: Any): Model3D {
        val cachedModel = obj as Model3D

        return cachedModel.copy()
    }

    override fun load(url: URL): Model3D {
        val isObj = url.toExternalForm().endsWith("obj")

        if (isObj) {
            return ObjModelLoader().load(url)
        }

        throw UnsupportedOperationException("Cannot load from URL: $url")
    }

    override fun getDummy(): Model3D = Model3D()
}

private class VideoAssetLoader : AssetLoader<MediaView>(
        MediaView::class.java,
        VIDEOS_DIR
) {

    override fun cast(obj: Any): MediaView {
        val mediaView = obj as MediaView

        val mediaPlayer = MediaPlayer(mediaView.mediaPlayer.media)

        return MediaView(mediaPlayer)
    }

    override fun load(url: URL): MediaView {
        return MediaView(MediaPlayer(Media(url.toExternalForm())))
    }

    override fun getDummy(): MediaView {
        return MediaView()
    }
}