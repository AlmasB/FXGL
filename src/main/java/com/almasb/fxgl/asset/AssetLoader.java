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
package com.almasb.fxgl.asset;

import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.parser.KVFile;
import com.almasb.fxgl.scene.CSS;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.FontFactory;
import com.almasb.fxgl.ui.UIController;
import com.almasb.fxgl.logging.FXGLLogger;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.text.Font;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Handles all resource (asset) loading operations.
 * <p>
 * "assets" directory must be located in source folder ("src" by default).
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
 * <li>CSS - /assets/ui/css/</li>
 * <li>Font - /assets/ui/fonts/</li>
 * <li>App icons - /assets/ui/icons/</li>
 * <li>Cursors - /assets/ui/cursors/</li>
 * </ul>
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Singleton
public class AssetLoader {

    private static final String ASSETS_DIR = "/assets/";
    private static final String TEXTURES_DIR = ASSETS_DIR + "textures/";
    private static final String SOUNDS_DIR = ASSETS_DIR + "sounds/";
    private static final String MUSIC_DIR = ASSETS_DIR + "music/";
    private static final String TEXT_DIR = ASSETS_DIR + "text/";
    private static final String KV_DIR = ASSETS_DIR + "kv/";
    private static final String BINARY_DIR = ASSETS_DIR + "data/";
    private static final String SCRIPTS_DIR = ASSETS_DIR + "scripts/";
    private static final String PROPERTIES_DIR = ASSETS_DIR + "properties/";

    private static final String UI_DIR = ASSETS_DIR + "ui/";
    private static final String CSS_DIR = UI_DIR + "css/";
    private static final String FONTS_DIR = UI_DIR + "fonts/";
    private static final String ICON_DIR = UI_DIR + "icons/";
    private static final String CURSORS_DIR = UI_DIR + "cursors/";

    private static final Logger log = FXGLLogger.getLogger("FXGL.AssetLoader");

    private final AssetsCache cachedAssets = new AssetsCache();

    @Inject
    private AssetLoader() {
        log.finer("Service [AssetLoader] initialized");
    }

    /**
     * Loads texture with given name from /assets/textures/.
     * Either returns a valid texture or throws an exception in case of errors.
     * <p>
     * Supported image formats are:
     * <ul>
     * <li><a href="http://msdn.microsoft.com/en-us/library/dd183376(v=vs.85).aspx">BMP</a></li>
     * <li><a href="http://www.w3.org/Graphics/GIF/spec-gif89a.txt">GIF</a></li>
     * <li><a href="http://www.ijg.org">JPEG</a></li>
     * <li><a href="http://www.libpng.org/pub/png/spec/">PNG</a></li>
     * </ul>
     * </p>
     *
     * @param name texture name without the /assets/textures/, e.g. "player.png"
     * @return texture
     * @throws IllegalArgumentException if asset not found or loading error
     */
    public Texture loadTexture(String name) {
        Object asset = getAssetFromCache(TEXTURES_DIR + name);
        if (asset != null) {
            return Texture.class.cast(asset).copy();
        }

        try (InputStream is = getStream(TEXTURES_DIR + name)) {
            Texture texture = new Texture(new Image(is));
            cachedAssets.put(TEXTURES_DIR + name, texture);
            return texture;
        } catch (Exception e) {
            throw loadFailed(name, e);
        }
    }

    /**
     * Loads sound with given name from /assets/sounds/.
     * Either returns a valid sound or throws an exception in case of errors.
     * <p>
     * Supported sound format:
     * <li>WAV</li>
     *
     * @param name sound name without the /assets/sounds/, e.g. "explosion.wav"
     * @return sound
     * @throws IllegalArgumentException if asset not found or loading error
     */
    public Sound loadSound(String name) {
        Object asset = getAssetFromCache(SOUNDS_DIR + name);
        if (asset != null) {
            return Sound.class.cast(asset);
        }

        try {
            Sound sound = new Sound(new AudioClip(getURL(SOUNDS_DIR + name).toExternalForm()));
            cachedAssets.put(SOUNDS_DIR + name, sound);
            return sound;
        } catch (Exception e) {
            throw loadFailed(name, e);
        }
    }

    /**
     * Loads sound with given name from /assets/music/.
     * Either returns a valid sound or throws an exception in case of errors.
     * <p>
     * Supported music format:
     * <li>MP3</li>
     *
     * @param name music name without the /assets/music/, e.g. "background_music.mp3"
     * @return music
     * @throws IllegalArgumentException if asset not found or loading error
     */
    public Music loadMusic(String name) {
        Object asset = getAssetFromCache(MUSIC_DIR + name);
        if (asset != null) {
            return Music.class.cast(asset);
        }

        try {
            Music music = new Music(new Media(getURL(MUSIC_DIR + name).toExternalForm()));
            cachedAssets.put(MUSIC_DIR + name, music);
            return music;
        } catch (Exception e) {
            throw loadFailed(name, e);
        }
    }

    /**
     * Loads text file with given name from /assets/text/
     * into List<String> where each element represents a line
     * in the file. Either returns a valid list with lines read from the file
     * or throws an exception in case of errors.
     *
     * @param name text file name without the /assets/text/, e.g. "level_0.txt"
     * @return list of lines from file
     * @throws IllegalArgumentException if asset not found or loading error
     */
    public List<String> loadText(String name) {
        Object asset = getAssetFromCache(TEXT_DIR + name);
        if (asset != null) {
            return (List<String>)asset;
        }

        List<String> text = readAllLines(TEXT_DIR + name);
        cachedAssets.put(TEXT_DIR + name, text);
        return text;
    }

    /**
     * Loads KVFile with given name from /assets/kv/.
     * Either returns a valid KVFile or throws exception in case of errors.
     *
     * @param name KVFile name without the /assets/kv/, .e.g "settings.kv"
     * @return kv file
     * @throws IllegalArgumentException if asset not found or loading error
     */
    public KVFile loadKV(String name) {
        return new KVFile(readAllLines(KV_DIR + name));
    }

    /**
     * Loads script with given name from /assets/scripts/ as a single string.
     * Either returns loaded string or throws exception in case of errors.
     *
     * @param name script file without the /assets/scripts/, e.g. "skill_heal.js"
     * @return script as a String
     * @throws IllegalArgumentException if asset not found or loading error
     */
    public String loadScript(String name) {
        StringBuilder builder = new StringBuilder();
        readAllLines(SCRIPTS_DIR + name)
                .forEach(line -> builder.append(line).append('\n'));
        return builder.toString();
    }

    /**
     * Loads resource bundle with given name from "/assets/properties/".
     *
     * @param name must be under "/assets/properties/", e.g. system.properties, game.properties
     * @return resource bundle
     * @throws IllegalArgumentException if asset not found or loading error
     */
    public ResourceBundle loadResourceBundle(String name) {
        try (InputStream is = getStream(PROPERTIES_DIR + name)) {
            return new PropertyResourceBundle(is);
        } catch (Exception e) {
            throw loadFailed(name, e);
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
    public Image loadCursorImage(String name) {
        try (InputStream is = getStream(CURSORS_DIR + name)) {
            return new Image(is);
        } catch (Exception e) {
            throw loadFailed(name, e);
        }
    }

    /**
     * Loads an FXML (.fxml) file from /assets/ui/.
     * Either returns a valid parsed UI or throws an exception in case of errors.
     *
     * @param name FXML file name
     * @param controller the controller object
     * @return a JavaFX UI parsed from .fxml
     * @throws IllegalArgumentException if asset not found or loading/parsing error
     */
    public Parent loadFXML(String name, UIController controller) {
        try (InputStream is = getStream(UI_DIR + name)) {
            FXMLLoader loader = new FXMLLoader();
            loader.setController(controller);
            Parent ui = loader.load(is);
            controller.init();
            return ui;
        } catch (Exception e) {
            throw loadFailed(name, e);
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
    public CSS loadCSS(String name) {
        try {
            return new CSS(getURL(CSS_DIR + name).toExternalForm());
        } catch (Exception e) {
            throw loadFailed(name, e);
        }
    }

    /**
     * Loads a native JavaFX font with given name from /assets/ui/fonts/
     * wrapped in a FontFactory, which later can be used to produce fonts
     * with different sizes without accessing the font file.
     * Either returns a valid font factory or throws exception in case of errors.
     * <p>
     * Supported font formats are:
     * <ul>
     * <li>TTF</li>
     * <li>OTF</li>
     * </ul>
     * </p>
     *
     * @param name font file name without the /assets/ui/fonts/, e.g. "quest_font.ttf"
     * @return font factory
     * @throws IllegalArgumentException if asset not found or loading error
     */
    public FontFactory loadFont(String name) {
        Object asset = getAssetFromCache(FONTS_DIR + name);
        if (asset != null) {
            return FontFactory.class.cast(asset);
        }

        try (InputStream is = getStream(FONTS_DIR + name)) {
            Font font = Font.loadFont(is, 12);
            if (font == null)
                font = Font.font(12);
            FontFactory fontFactory = new FontFactory(font);
            cachedAssets.put(FONTS_DIR + name, fontFactory);
            return fontFactory;
        } catch (Exception e) {
            throw loadFailed(name, e);
        }
    }

    /**
     * Loads an app icon from /assets/ui/icons/.
     * Either returns a valid image or throws an exception in case of errors.
     *
     * @param name image name without the /assets/ui/icons/, e.g. "app_icon.png"
     * @return app icon image
     * @throws IllegalArgumentException if asset not found or loading error
     */
    public Image loadAppIcon(String name) {
        try (InputStream is = getStream(ICON_DIR + name)) {
            return new Image(is);
        } catch (Exception e) {
            throw loadFailed(name, e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T loadDataInternal(String name) {
        try (ObjectInputStream ois = new ObjectInputStream(getStream(BINARY_DIR + name))) {
            return (T) ois.readObject();
        } catch (Exception e) {
            throw loadFailed(name, e);
        }
    }

    /**
     * Returns a valid URL to resource or throws {@link IllegalArgumentException}.
     *
     * @param name resource name
     * @return URL to resource
     */
    private URL getURL(String name) {
        log.finer("Loading from disk: " + name);

        URL url = getClass().getResource(name);
        if (url == null) {
            throw new IllegalArgumentException("Asset \"" + name + "\" was not found!");
        }

        return url;
    }

    /**
     * Opens a stream to resource with given name. The caller is responsible for
     * closing the stream. Either returns a valid stream or throws an exception.
     *
     * @param name resource name
     * @return resource stream
     * @throws IllegalArgumentException if any error occurs or stream is null
     */
    private InputStream getStream(String name) {
        try {
            InputStream is = getURL(name).openStream();
            if (is == null)
                throw new IOException("Input stream to \"" + name + "\" is null!");
            return is;
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to obtain input stream to URL: " + e.getMessage());
        }
    }

    /**
     * Load an asset from cache.
     *
     * @param name asset name
     * @return asset object or null if not found
     */
    private Object getAssetFromCache(String name) {
        Object asset = cachedAssets.get(name);
        if (asset != null) {
            log.finer("Loading from cache: " + name);
            return asset;
        } else {
            return null;
        }
    }

    /**
     * Read all lines from a file. Bytes from the file are decoded into characters
     * using the {@link StandardCharsets#UTF_8 UTF-8} {@link Charset charset}.
     *
     * @param name resource name
     * @return the lines from the file as a {@code List}
     */
    private List<String> readAllLines(String name) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getStream(name)))) {
            List<String> result = new ArrayList<>();

            for (; ; ) {
                String line = reader.readLine();
                if (line == null)
                    break;
                result.add(line);
            }
            return result;
        } catch (Exception e) {
            throw loadFailed(name, e);
        }
    }

    /**
     * Pre-loads all textures / sounds / music / text / fonts and binary data
     * from their respective folders.
     */
    public void cache() {
        try {
            loadFileNames(TEXTURES_DIR).forEach(this::loadTexture);
            loadFileNames(SOUNDS_DIR).forEach(this::loadSound);
            loadFileNames(MUSIC_DIR).forEach(this::loadMusic);
            loadFileNames(TEXT_DIR).forEach(this::loadText);
            loadFileNames(FONTS_DIR).forEach(this::loadFont);
            loadFileNames(BINARY_DIR).forEach(this::loadDataInternal);
        } catch (Exception e) {
            throw loadFailed("Caching Failed", e);
        }
    }

    /**
     * Release all cached assets.
     */
    public void clearCache() {
        log.finer("Clearing assets cache");
        cachedAssets.clear();
    }

    /**
     * Loads file names from a directory.
     * Note: directory name must be in the format "/assets/...".
     * Returned file names are relativized to the given directory name.
     *
     * @param directory name of directory
     * @return list of file names
     * @throws Exception
     */
    public List<String> loadFileNames(String directory) throws Exception {
        URL url = getClass().getResource(directory);
        if (url != null) {
            if (url.toString().startsWith("jar"))
                return loadFileNamesJar(directory.substring(1));

            Path dir = Paths.get(url.toURI());

            if (Files.exists(dir)) {
                try (Stream<Path> files = Files.walk(dir)) {
                    return files.filter(Files::isRegularFile)
                            .map(file -> dir.relativize(file).toString().replace("\\", "/"))
                            .collect(Collectors.toList());
                }
            }
        }

        return loadFileNamesJar(directory.substring(1));
    }

    /**
     * Loads file names from a directory when running within a jar.
     * If it contains other folders they'll be searched too.
     *
     * @param folderName folder files of which need to be retrieved
     * @return list of file names
     */
    private static List<String> loadFileNamesJar(String folderName) {
        List<String> fileNames = new ArrayList<>();
        CodeSource src = AssetLoader.class.getProtectionDomain().getCodeSource();
        if (src != null) {
            URL jar = src.getLocation();
            try (InputStream is = jar.openStream();
                 ZipInputStream zip = new ZipInputStream(is)) {
                ZipEntry ze;
                while ((ze = zip.getNextEntry()) != null) {
                    String entryName = ze.getName();
                    if (entryName.startsWith(folderName)) {
                        if (entryName.endsWith("/"))
                            continue;
                        fileNames.add(entryName.substring(entryName.indexOf(folderName) + folderName.length()));
                    }
                }
            } catch (IOException e) {
                log.warning("Failed to load file names from jar - " + e.getMessage());
            }
        } else {
            log.warning("Failed to load file names from jar - No code source");
        }

        return fileNames;
    }

    /**
     * Constructs new IllegalArgumentException with "load failed" message
     * and with relevant information about the asset.
     *
     * @param assetName name of the asset load of which failed
     * @param error the error that occurred
     * @return instance of IAE to be thrown
     */
    private IllegalArgumentException loadFailed(String assetName, Throwable error) {
        log.finer("Loading failed for asset: " + assetName + ". Cause: " + error.getMessage());
        return new IllegalArgumentException("Failed to load asset: " + assetName + ". Cause: " + error.getMessage());
    }
}
