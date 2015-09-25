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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.almasb.fxgl.util.FXGLLogger;

/**
 * Stores cached assets.
 */
public final class Assets {

    private static final Logger log = FXGLLogger.getLogger("FXGL.Assets");

    private Map<String, Texture> cachedTextures = new HashMap<>();
    private Map<String, Sound> cachedSounds = new HashMap<>();
    private Map<String, Music> cachedMusic = new HashMap<>();
    private Map<String, List<String> > cachedText = new HashMap<>();
    private Map<String, FontFactory> cachedFonts = new HashMap<>();
    private Map<String, Object> cachedData = new HashMap<>();

    /**
     * Prevent from instantiation outside of FXGL
     */
    /*package-private*/ Assets() {

    }

    /*package-private*/ void putTexture(String key, Texture texture) {
        cachedTextures.put(key, texture);
    }

    /*package-private*/ void putSound(String key, Sound sound) {
        cachedSounds.put(key, sound);
    }

    /*package-private*/ void putMusic(String key, Music music) {
        cachedMusic.put(key, music);
    }

    /*package-private*/ void putText(String key, List<String> text) {
        cachedText.put(key, text);
    }

    /*package-private*/ void putFontFactory(String key, FontFactory fontFactory) {
        cachedFonts.put(key, fontFactory);
    }

    /*package-private*/ void putData(String key, Object data) {
        cachedData.put(key, data);
    }

    /**
     * Returns a new copy of a cached texture so
     * it is safe to use multiple times.
     *
     * @param key
     * @return
     * @throws IllegalArgumentException if no such texture exists
     */
    public Texture getTexture(String key) {
        Texture original = cachedTextures.get(key);
        if (original != null)
            return original.copy();
        else
            throw new IllegalArgumentException("No cached texture found with name: " + key);
    }

    /**
     * Returns stored audio clip.
     *
     * @param key
     * @return
     * @throws IllegalArgumentException if no such sound exists
     */
    public Sound getSound(String key) {
        Sound sound = cachedSounds.get(key);
        if (sound != null)
            return sound;
        else
            throw new IllegalArgumentException("No cached sound found with name: " + key);
    }

    /**
     * Returns stored music object.
     *
     * @param key
     * @return
     * @throws IllegalArgumentException if no such music exists
     */
    public Music getMusic(String key) {
        Music music = cachedMusic.get(key);
        if (music != null)
            return music;
        else
            throw new IllegalArgumentException("No cached music found with name: " + key);
    }

    /**
     * Returns new list contains original text.
     *
     * @param key
     * @return
     * @throws IllegalArgumentException if no such text exists
     */
    public List<String> getText(String key) {
        List<String> text = cachedText.get(key);
        if (text != null)
            return new ArrayList<>(text);
        else
            throw new IllegalArgumentException("No cached text found with name: " + key);
    }

    /**
     * Returns stored font.
     *
     * @param key
     * @return
     */
    public FontFactory getFontFactory(String key) {
        FontFactory fontFactory = cachedFonts.get(key);
        if (fontFactory != null)
            return fontFactory;
        else
            throw new IllegalArgumentException("No cached font factory found with name: " + key);
    }

    /**
     * Returns cached custom format data.
     *
     * @param key
     * @return
     * @throws IllegalArgumentException if no such data exists
     */
    @SuppressWarnings("unchecked")
    public <T> T getData(String key) {
        Object data = cachedData.get(key);
        if (data != null)
            return (T) data;
        else
            throw new IllegalArgumentException("No cached data found with name: " + key);
    }

    /**
     * A convenience method to print all cached assets.
     * Useful for debugging.
     */
    public void logCached() {
        log.info("Logging cached assets");
        cachedTextures.forEach((name, texture) -> log.info("Texture:" + name));
        cachedSounds.forEach((name, sound) -> log.info("Sound:" + name));
        cachedMusic.forEach((name, music) -> log.info("Music:" + name));
        cachedText.forEach((name, text) -> log.info("Text:" + name));
        cachedFonts.forEach((name, font) -> log.info("Font:" + name));
        cachedData.forEach((name, data) -> log.info("Data:" + name));
    }

    /**
     *
     * @return  size of all cached assets
     */
    public int size() {
        return cachedTextures.size() + cachedSounds.size() + cachedMusic.size()
            + cachedText.size() + cachedFonts.size() + cachedData.size();
    }
}
