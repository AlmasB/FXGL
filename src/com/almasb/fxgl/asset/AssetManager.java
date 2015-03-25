package com.almasb.fxgl.asset;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import com.almasb.fxgl.FXGLLogger;

import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;

public class AssetManager {

    private static final String ASSETS_DIR = "/assets/";
    private static final String TEXTURES_DIR = ASSETS_DIR + "textures/";
    private static final String AUDIO_DIR = ASSETS_DIR + "audio/";
    private static final String MUSIC_DIR = ASSETS_DIR + "music/";

    private static final Logger log = FXGLLogger.getLogger("AssetManager");

    public Texture loadTexture(String name) {
        Texture texture = new Texture();

        try (InputStream is = getClass().getResourceAsStream(TEXTURES_DIR + name)) {
            if (is != null) {
                Image image = new Image(is);
                texture.setImage(image);
            }
            else {
                log.warning("Failed to load texture: " + name + " Check it exists in assets/textures/");
            }
        }
        catch (IOException e) {
            log.warning("Failed to load texture: " + name);
            FXGLLogger.trace(e);
        }

        return texture;
    }

    public Audio loadAudio(String name) {
        Audio audio = new Audio();

        try {
            AudioClip clip = new AudioClip(getClass().getResource(AUDIO_DIR + name).toExternalForm());
            audio.setAudioClip(clip);
        }
        catch (Exception e) {
            log.warning("Failed to load audio: " + name + " Check it exists in assets/audio/");
            FXGLLogger.trace(e);
        }

        return audio;
    }

    public Music loadMusic(String name) {
        Music music = new Music();

        try {
            music.setMedia(new Media(getClass().getResource(MUSIC_DIR + name).toExternalForm()));
        }
        catch (Exception e) {
            log.warning("Failed to load music: " + name + " Check it exists in assets/music/");
            FXGLLogger.trace(e);
        }

        return music;
    }
}
