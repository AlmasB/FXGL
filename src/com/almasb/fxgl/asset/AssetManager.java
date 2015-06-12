package com.almasb.fxgl.asset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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

import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;

import com.almasb.fxgl.FXGLLogger;

/**
 * AssetManager handles all resource (asset) loading operations
 *
 * "assets" directory must be located in source folder - "src" by default
 *
 * AssetManager will look for resources (assets) under these specified directories
 * <ul>
 * <li>Texture - /assets/textures/</li>
 * <li>AudioClip - /assets/audio/</li>
 * <li>Music - /assets/music/</li>
 * </ul>
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 * @version 1.0
 *
 */
public class AssetManager {

    private static final String ASSETS_DIR = "/assets/";
    private static final String TEXTURES_DIR = ASSETS_DIR + "textures/";
    private static final String AUDIO_DIR = ASSETS_DIR + "audio/";
    private static final String MUSIC_DIR = ASSETS_DIR + "music/";
    private static final String TEXT_DIR = ASSETS_DIR + "text/";
    private static final String BINARY_DIR = ASSETS_DIR + "data/";

    private static final Logger log = FXGLLogger.getLogger("AssetManager");

    public Texture loadTexture(String name) throws Exception {
        try (InputStream is = getClass().getResourceAsStream(TEXTURES_DIR + name)) {
            if (is != null) {
                return new Texture(new Image(is));
            }
            else {
                log.warning("Failed to load texture: " + name + " Check it exists in assets/textures/");
                throw new IOException("Failed to load texture");
            }
        }
    }

    public AudioClip loadAudio(String name) throws Exception {
        return new AudioClip(getClass().getResource(AUDIO_DIR + name).toExternalForm());
    }

    public Music loadMusic(String name) throws Exception {
        return new Music(new Media(getClass().getResource(MUSIC_DIR + name).toExternalForm()));
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

    /**
     * Save serializable data onto a disk file system
     *
     * @param data
     * @param fileName
     * @throws Exception
     */
    public void saveData(Serializable data, String fileName) throws Exception {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Paths.get(fileName)))) {
            oos.writeObject(data);
        }
    }

    /**
     * Load serializable data from external (NOT jar where the app is running from)
     * file on disk file system
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public <T> T loadData(String fileName) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Paths.get(fileName)))) {
            return (T)ois.readObject();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T loadDataInternal(String name) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(getClass().getResourceAsStream(BINARY_DIR + name))) {
            return (T)ois.readObject();
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
