package com.almasb.fxgl.asset;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

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

    public void saveData(Serializable data, String fileName) throws Exception {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Paths.get(fileName)))) {
            oos.writeObject(data);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T loadData(String fileName) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Paths.get(fileName)))) {
            return (T)ois.readObject();
        }
    }
}
