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

import com.almasb.fxgl.FXGLLogger;

import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;

/**
 * AssetManager handles all resource (asset) loading operations.
 *
 * "assets" directory must be located in source folder ("src" by default).
 *
 * AssetManager will look for resources (assets) under these specified directories
 * <ul>
 * <li>Texture - /assets/textures/</li>
 * <li>AudioClip - /assets/audio/</li>
 * <li>Music - /assets/music/</li>
 * <li>Text (List&lt;String&gt;) - /assets/text/</li>
 * <li>Data - /assets/data/</li>
 * <li>CSS - /assets/ui/css/</li>
 * <li>App icons - /assets/ui/icons/</li>
 * </ul>
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 * @version 1.0
 *
 */
public enum AssetManager {
    INSTANCE;

    private static final String ASSETS_DIR = "/assets/";
    private static final String TEXTURES_DIR = ASSETS_DIR + "textures/";
    private static final String AUDIO_DIR = ASSETS_DIR + "audio/";
    private static final String MUSIC_DIR = ASSETS_DIR + "music/";
    private static final String TEXT_DIR = ASSETS_DIR + "text/";
    private static final String BINARY_DIR = ASSETS_DIR + "data/";
    private static final String UI_DIR = ASSETS_DIR + "ui/";
    private static final String CSS_DIR = UI_DIR + "css/";
    private static final String ICON_DIR = UI_DIR + "icons/";

    private static final Logger log = FXGLLogger.getLogger("AssetManager");

    public Texture loadTexture(String name) throws Exception {
        try (InputStream is = getClass().getResourceAsStream(TEXTURES_DIR + name)) {
            if (is != null) {
                return new Texture(new Image(is));
            }
            else {
                log.warning("Failed to load texture: " + name + " Check it exists in assets/textures/");
                throw new IOException("Failed to load texture: " + name);
            }
        }
    }

    public AudioClip loadAudio(String name) throws Exception {
        try {
            return new AudioClip(getClass().getResource(AUDIO_DIR + name).toExternalForm());
        }
        catch (Exception e) {
            log.warning("Failed to load audio: " + name + " Check it exists in assets/audio/");
            throw new IOException("Failed to load audio: " + name);
        }
    }

    public Music loadMusic(String name) throws Exception {
        try {
            return new Music(new Media(getClass().getResource(MUSIC_DIR + name).toExternalForm()));
        }
        catch (Exception e) {
            log.warning("Failed to load music: " + name + " Check it exists in assets/music/");
            throw new IOException("Failed to load music: " + name);
        }
    }

    public List<String> loadText(String name) throws Exception {
        try (InputStream is = getClass().getResourceAsStream(TEXT_DIR + name);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            List<String> result = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
            return result;
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
        try (ObjectInputStream ois = new ObjectInputStream(getClass().getResourceAsStream(BINARY_DIR + name))) {
            return (T)ois.readObject();
        }
    }

    /**
     * Returns external form of of URL to CSS file ready to be applied to UI elements.
     * Can be applied by calling object.getStyleSheets().add()
     *
     * @param name
     * @return
     * @throws Exception
     */
    public String loadCSS(String name) throws Exception {
        try {
            return getClass().getResource(CSS_DIR + name).toExternalForm();
        }
        catch (Exception e) {
            log.warning("Failed to load css: " + name + " Check it exists in assets/ui/css/");
            throw new IOException("Failed to load css: " + name);
        }
    }

    /**
     * Loads an app icon from assets/ui/icons.
     *
     * @param name
     * @return
     * @throws Exception
     */
    public Image loadAppIcon(String name) throws Exception {
        try (InputStream is = getClass().getResourceAsStream(ICON_DIR + name)) {
            if (is != null) {
                return new Image(is);
            }
            else {
                log.warning("Failed to load icon: " + name + " Check it exists in assets/ui/icons/");
                throw new IOException("Failed to load icon: " + name);
            }
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
        List<String> audio = loadFileNames(AUDIO_DIR);
        List<String> music = loadFileNames(MUSIC_DIR);
        List<String> text = loadFileNames(TEXT_DIR);
        List<String> data = loadFileNames(BINARY_DIR);

        Assets assets = new Assets();
        for (String name : textures)
            assets.putTexture(name, loadTexture(name));
        for (String name : audio)
            assets.putAudio(name, loadAudio(name));
        for (String name : music)
            assets.putMusic(name, loadMusic(name));
        for (String name : text)
            assets.putText(name, loadText(name));
        for (String name : data)
            assets.putData(name, loadDataInternal(name));

        return assets;
    }

    /**
     * Loads file names from a directory
     *
     * @param directory
     * @return list of file names
     * @throws Exception
     */
    private List<String> loadFileNames(String directory) throws Exception {
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
     *
     * If it contains other folders they'll be searched too
     *
     * @param folderName
     *            folder files of which need to be retrieved
     * @return list of filenames
     */
    private static List<String> loadFileNamesJar(String folderName) {
        List<String> fileNames = new ArrayList<>();
        CodeSource src = AssetManager.class.getProtectionDomain().getCodeSource();
        if (src != null) {
            URL jar = src.getLocation();
            try (InputStream is = jar.openStream();
                    ZipInputStream zip = new ZipInputStream(is)) {
                ZipEntry ze = null;
                while ((ze = zip.getNextEntry()) != null) {
                    String entryName = ze.getName();
                    if (entryName.startsWith(folderName)) {
                        if (entryName.endsWith("/"))
                            continue;
                        fileNames.add(entryName.substring(entryName.indexOf(folderName) + folderName.length()));
                    }
                }
            }
            catch (IOException e) {
                log.warning("Failed to load file names from jar - " + e.getMessage());
            }
        }
        else {
            log.warning("Failed to load file names from jar - No code source");
        }

        return fileNames;
    }
}
