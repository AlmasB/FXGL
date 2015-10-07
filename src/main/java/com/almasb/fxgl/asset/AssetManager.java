/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.almasb.fxgl.util.FXGLLogger;

import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.text.Font;

/**
 * AssetManager handles all resource (asset) loading operations.
 * <p>
 * "assets" directory must be located in source folder ("src" by default).
 * <p>
 * AssetManager will look for resources (assets) under these specified directories
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
public enum AssetManager {
    INSTANCE;

    private static final String ASSETS_DIR = "/assets/";
    private static final String TEXTURES_DIR = ASSETS_DIR + "textures/";
    private static final String SOUNDS_DIR = ASSETS_DIR + "sounds/";
    private static final String MUSIC_DIR = ASSETS_DIR + "music/";
    private static final String TEXT_DIR = ASSETS_DIR + "text/";
    private static final String KV_DIR = ASSETS_DIR + "kv/";
    private static final String BINARY_DIR = ASSETS_DIR + "data/";
    private static final String SCRIPTS_DIR = ASSETS_DIR + "scripts/";

    private static final String UI_DIR = ASSETS_DIR + "ui/";
    private static final String CSS_DIR = UI_DIR + "css/";
    private static final String FONTS_DIR = UI_DIR + "fonts/";
    private static final String ICON_DIR = UI_DIR + "icons/";
    private static final String CURSORS_DIR = UI_DIR + "cursors/";

    private static final Logger log = FXGLLogger.getLogger("FXGL.AssetManager");

    /**
     * Loads texture with given name from {@value #TEXTURES_DIR}.
     * Either returns a valid texture or throws an exception in case of errors.
     * <p>
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
     * @param name texture name without the {@value #TEXTURES_DIR}, e.g. "player.png"
     * @return texture
     * @throws IllegalArgumentException if asset not found or loading error
     */
    public Texture loadTexture(String name) {
        try (InputStream is = getStream(TEXTURES_DIR + name)) {
            return new Texture(new Image(is));
        } catch (Exception e) {
            throw loadFailed(name, e);
        }
    }

    /**
     * Loads sound with given name from {@value #SOUNDS_DIR}.
     * Either returns a valid sound or throws an exception in case of errors.
     * <p>
     * Supported sound format:
     * <li>WAV</li>
     *
     * @param name sound name without the {@value #SOUNDS_DIR}, e.g. "explosion.wav"
     * @return sound
     * @throws IllegalArgumentException if asset not found or loading error
     */
    public Sound loadSound(String name) {
        try {
            return new Sound(new AudioClip(getURL(SOUNDS_DIR + name).toExternalForm()));
        } catch (Exception e) {
            throw loadFailed(name, e);
        }
    }

    /**
     * Loads sound with given name from {@value #MUSIC_DIR}.
     * Either returns a valid sound or throws an exception in case of errors.
     * <p>
     * Supported sound format:
     * <li>MP3</li>
     *
     * @param name music name without the {@value #MUSIC_DIR}, e.g. "background_music.mp3"
     * @return music
     * @throws IllegalArgumentException if asset not found or loading error
     */
    public Music loadMusic(String name) {
        try {
            return new Music(new Media(getURL(MUSIC_DIR + name).toExternalForm()));
        } catch (Exception e) {
            throw loadFailed(name, e);
        }
    }

    /**
     * Loads text file with given name from {@value #TEXT_DIR}
     * into List<String> where each element represents a line
     * within the file. Either returns a valid list with lines read from the file
     * or throws an exception in case of errors
     *
     * @param name text file name without the {@value #TEXT_DIR}, e.g. "level_0.txt"
     * @return list of lines from file
     * @throws IllegalArgumentException if asset not found or loading error
     */
    public List<String> loadText(String name) {
        return readAllLines(TEXT_DIR + name);
    }

    /**
     * Loads KVFile with given name from {@value #KV_DIR}.
     * Either returns a valid KVFile or throws exception in case of errors.
     *
     * @param name KVFile name without the {@value #KV_DIR}, .e.g "settings.kv"
     * @return kv file
     * @throws IllegalArgumentException if asset not found or loading error
     */
    public KVFile loadKV(String name) {
        return new KVFile(readAllLines(KV_DIR + name));
    }

    /**
     * Loads script with given name from {@value #SCRIPTS_DIR} as a single string.
     * Either returns loaded string or throws exception in case of errors.
     *
     * @param name script file without the {@value #SCRIPTS_DIR}, e.g. "skill_heal.js"
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
     * Loads cursor image with given name from {@value #CURSORS_DIR}.
     * Either returns a valid image or throws exception in case of errors.
     *
     * @param name image name without the {@value #CURSORS_DIR}, e.g. "attack_cursor.png"
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
     * Returns external form of of URL to CSS file (from {@value #CSS_DIR} ready to be applied to UI elements.
     * Can be applied by calling object.getStyleSheets().add().
     * Either returns ready CSS or throws exception in case of errors.
     *
     * @param name CSS file name without the {@value #CSS_DIR}, e.g. "ui_button.css"
     * @return css URL external form
     * @throws IllegalArgumentException if asset not found or loading error
     */
    public String loadCSS(String name) {
        try {
            return getURL(CSS_DIR + name).toExternalForm();
        } catch (Exception e) {
            throw loadFailed(name, e);
        }
    }

    /**
     * Loads a native JavaFX font with given name from {@value #FONTS_DIR}
     * wrapped in a FontFactory, which later can be used to produce fonts
     * with different sizes without accessing the font file.
     * Either returns a valid font factory or throws exception in case of errors
     * <p>
     * <p>
     * Supported font formats are:
     * <ul>
     * <li>TTF</li>
     * <li>OTF</li>
     * </ul>
     * </p>
     *
     * @param name font file name without the {@value #FONTS_DIR}, e.g. "quest_font.ttf"
     * @return font factory
     * @throws IllegalArgumentException if asset not found or loading error
     */
    public FontFactory loadFont(String name) {
        try (InputStream is = getStream(FONTS_DIR + name)) {
			Font font = Font.loadFont(is, 12);
			if (font == null)
				font = Font.font(12);
			return new FontFactory(font);
        } catch (Exception e) {
            throw loadFailed(name, e);
        }
    }

    /**
     * Loads an app icon from {@value #ICON_DIR}.
     * Either returns a valid image or throws an exception in case of errors.
     *
     * @param name image name without the {@value #ICON_DIR}, e.g. "app_icon.png"
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

//    public Parent loadUI(String name) throws Exception {
//        try {
//            URL url = getClass().getResource(UI_DIR + name);
//            if (url == null) {
//                log.warning("Failed to load UI: " + name + " Check it exists in assets/ui/");
//                throw new IOException("Failed to load UI: " + name);
//            }
//
//            return FXMLLoader.load(url);
//        }
//        catch (Exception e) {
//            log.warning("Failed to load UI: " + name + " Check file for syntax errors");
//            throw new IOException("Failed to load UI: " + name);
//        }
//    }
//
//    public Parent loadUI(String name, Object controller) throws Exception {
//        try {
//            URL url = getClass().getResource(UI_DIR + name);
//            if (url == null) {
//                log.warning("Failed to load UI: " + name + " Check it exists in assets/ui/");
//                throw new IOException("Failed to load UI: " + name);
//            }
//
//            FXMLLoader loader = new FXMLLoader(url);
//            loader.setController(controller);
//
//            return loader.load();
//        }
//        catch (Exception e) {
//            log.warning("Failed to load UI: " + name + " Check file for syntax errors");
//            log.warning("Error message: " + e.getMessage());
//            throw new IOException("Failed to load UI: " + name);
//        }
//    }

    @SuppressWarnings("unchecked")
    private <T> T loadDataInternal(String name) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(getStream(BINARY_DIR + name))) {
            return (T) ois.readObject();
        }
    }

    /**
     * Returns a valid URL to resource or throws {@link IllegalArgumentException}.
     *
     * @param name resource name
     * @return URL to resource
     */
    private URL getURL(String name) {
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
     * Pre-loads all textures / audio / music from
     * their respective folders
     *
     * @return assets object holding cached resources
     * @throws Exception
     */
    public Assets cache() throws Exception {
        List<String> textures = loadFileNames(TEXTURES_DIR);
        List<String> sounds = loadFileNames(SOUNDS_DIR);
        List<String> music = loadFileNames(MUSIC_DIR);
        List<String> text = loadFileNames(TEXT_DIR);
        List<String> fonts = loadFileNames(FONTS_DIR);
        List<String> data = loadFileNames(BINARY_DIR);

        Assets assets = new Assets();
        for (String name : textures)
            assets.putTexture(name, loadTexture(name));
        for (String name : sounds)
            assets.putSound(name, loadSound(name));
        for (String name : music)
            assets.putMusic(name, loadMusic(name));
        for (String name : text)
            assets.putText(name, loadText(name));
        for (String name : data)
            assets.putData(name, loadDataInternal(name));
        for (String name : fonts)
            assets.putFontFactory(name, loadFont(name));

        return assets;
    }

    /**
     * Loads file names from a directory.
     * <p>
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
     * Loads file names from a directory when running within a jar
     * <p>
     * If it contains other folders they'll be searched too
     *
     * @param folderName folder files of which need to be retrieved
     * @return list of filenames
     */
    private static List<String> loadFileNamesJar(String folderName) {
        List<String> fileNames = new ArrayList<>();
        CodeSource src = AssetManager.class.getProtectionDomain().getCodeSource();
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
        return new IllegalArgumentException("Failed to load asset: " + assetName + ". Cause: " + error.getMessage());
    }
}
