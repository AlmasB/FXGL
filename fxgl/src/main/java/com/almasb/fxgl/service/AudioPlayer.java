/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.service;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.listener.UpdateListener;
import com.almasb.fxgl.asset.FXGLAssets;
import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.gameplay.NotificationEvent;
import com.almasb.fxgl.gameplay.NotificationListener;
import com.almasb.fxgl.saving.UserProfileSavable;
import javafx.beans.property.DoubleProperty;

/**
 * General audio player service that supports playback of sound and music objects.
 * It can also control volume of both.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public interface AudioPlayer extends UserProfileSavable, UpdateListener, NotificationListener {

    @Override
    default void onNotificationEvent(NotificationEvent event) {
        playSound(FXGLAssets.SOUND_NOTIFICATION);
    }

    /**
     * @return global music volume property
     */
    DoubleProperty globalMusicVolumeProperty();

    /**
     * @return global music volume
     */
    default double getGlobalMusicVolume() {
        return globalMusicVolumeProperty().get();
    }

    /**
     * Set global music volume in the range [0..1],
     * where 0 = 0%, 1 = 100%.
     *
     * @param volume music volume
     */
    default void setGlobalMusicVolume(double volume) {
        globalMusicVolumeProperty().set(volume);
    }

    /**
     * @return global sound volume property
     */
    DoubleProperty globalSoundVolumeProperty();

    /**
     * @return global sound volume
     */
    default double getGlobalSoundVolume() {
        return globalSoundVolumeProperty().get();
    }

    /**
     * Set global sound volume in the range [0..1],
     * where 0 = 0%, 1 = 100%.
     *
     * @param volume sound volume
     */
    default void setGlobalSoundVolume(double volume) {
        globalSoundVolumeProperty().set(volume);
    }

    /**
     * Convenience method to play the sound given its filename.
     *
     * @param assetName name of the sound file
     */
    default void playSound(String assetName) {
        playSound(FXGL.getAssetLoader().loadSound(assetName));
    }

    /**
     * Convenience method to play the music given its filename.
     *
     * @param assetName name of the music file
     */
    default void playMusic(String assetName) {
        playMusic(FXGL.getAssetLoader().loadMusic(assetName));
    }

    /**
     * Plays given sound based on its properties.
     *
     * @param sound sound to play
     */
    void playSound(Sound sound);

    /**
     * Stops playing given sound.
     *
     * @param sound sound to stop
     */
    void stopSound(Sound sound);

    /**
     * Stops playing all sounds.
     */
    void stopAllSounds();

    /**
     * Plays given music based on its properties.
     * If the music has been paused, you need to call {@link #resumeMusic(Music)} instead.
     *
     * @param music music to play
     * @throws IllegalArgumentException if the music is already playing / paused
     */
    void playMusic(Music music);

    /**
     * Pauses given music if it was previously started with {@link #playSound(Sound)}.
     * It can then be restarted by {@link #resumeMusic(Music)}.
     *
     * @param music music to pause
     */
    void pauseMusic(Music music);

    /**
     * Resumes previously paused {@link #pauseMusic(Music)} music.
     *
     * @param music music to resume
     */
    void resumeMusic(Music music);

    /**
     * Stops currently playing music. It cannot be restarted
     * using {@link #resumeMusic(Music)}. The music object needs
     * to be started again by {@link #playMusic(Music)}.
     *
     * @param music music to stop
     */
    void stopMusic(Music music);

    /**
     * Pauses all currently playing music. These can be
     * resumed using {@link #resumeAllMusic()}.
     */
    void pauseAllMusic();

    /**
     * Resumes all currently paused music.
     */
    void resumeAllMusic();

    /**
     * Stops all currently playing music. The music cannot be restarted
     * by calling {@link #resumeAllMusic()}. Each music object will need
     * to be started by {@link #playMusic(Music)}.
     */
    void stopAllMusic();
}
