package com.almasb.fxgl.asset;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.media.AudioClip;

/**
 * Stores cached data
 */
public final class Assets {

    private Map<String, Texture> cachedTextures = new HashMap<>();
    private Map<String, AudioClip> cachedAudio = new HashMap<>();
    private Map<String, Music> cachedMusic = new HashMap<>();

    /*package-private*/ Assets() {

    }

    /*package-private*/ void putTexture(String key, Texture texture) {
        cachedTextures.put(key, texture);
    }

    /*package-private*/ void putAudio(String key, AudioClip audio) {
        cachedAudio.put(key, audio);
    }

    /*package-private*/ void putMusic(String key, Music music) {
        cachedMusic.put(key, music);
    }

    public Texture getTexture(String key) {
        return cachedTextures.get(key);
    }

    public AudioClip getAudio(String key) {
        return cachedAudio.get(key);
    }

    public Music getMusic(String key) {
        return cachedMusic.get(key);
    }
}
