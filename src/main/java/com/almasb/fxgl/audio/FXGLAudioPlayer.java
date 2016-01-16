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

package com.almasb.fxgl.audio;

import com.almasb.fxeventbus.EventBus;
import com.almasb.fxgl.asset.FXGLAssets;
import com.almasb.fxgl.event.LoadEvent;
import com.almasb.fxgl.event.NotificationEvent;
import com.almasb.fxgl.event.SaveEvent;
import com.almasb.fxgl.event.UpdateEvent;
import com.almasb.fxgl.settings.UserProfile;
import com.almasb.fxgl.settings.UserProfileSavable;
import com.almasb.fxgl.util.FXGLLogger;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * FXGL provider of audio service.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Singleton
public final class FXGLAudioPlayer implements AudioPlayer, UserProfileSavable {

    private static final Logger log = FXGLLogger.getLogger("FXGL.AudioPlayer");

    @Inject
    private FXGLAudioPlayer(EventBus eventBus) {
        eventBus.addEventHandler(UpdateEvent.ANY, event -> {
            activeMusic.stream()
                    .filter(music -> music.mediaPlayer.getCurrentTime().equals(music.mediaPlayer.getTotalDuration()))
                    .forEach(music -> music.isStopped = true);

            activeSounds.removeIf(sound -> !sound.clip.isPlaying());
            activeMusic.removeIf(music -> music.isStopped);
        });
        eventBus.addEventHandler(NotificationEvent.ANY, event -> {
            playSound(FXGLAssets.SOUND_NOTIFICATION);
        });

        eventBus.addEventHandler(SaveEvent.ANY, event -> {
            save(event.getProfile());
        });

        eventBus.addEventHandler(LoadEvent.ANY, event -> {
            load(event.getProfile());
        });

        log.finer("Service [AudioPlayer] initialized");
    }

    /**
     * Contains sounds which are currently playing.
     */
    private List<Sound> activeSounds = new ArrayList<>();

    /**
     * Contains music objects which are currently playing or paused.
     */
    private List<Music> activeMusic = new ArrayList<>();

    private DoubleProperty globalMusicVolume = new SimpleDoubleProperty(0.5);

    /**
     * @return global music volume property
     */
    @Override
    public DoubleProperty globalMusicVolumeProperty() {
        return globalMusicVolume;
    }

    private DoubleProperty globalSoundVolume = new SimpleDoubleProperty(0.5);

    /**
     * @return global sound volume property
     */
    @Override
    public DoubleProperty globalSoundVolumeProperty() {
        return globalSoundVolume;
    }

    /**
     * Plays given sound based on its properties.
     *
     * @param sound sound to play
     */
    @Override
    public void playSound(Sound sound) {
        if (!activeSounds.contains(sound))
            activeSounds.add(sound);
        sound.clip.volumeProperty().bind(globalSoundVolumeProperty());
        sound.clip.play();
    }

    /**
     * Stops playing given sound.
     *
     * @param sound sound to stop
     */
    @Override
    public void stopSound(Sound sound) {
        activeSounds.remove(sound);
        sound.clip.stop();
    }

    /**
     * Stops playing all sounds.
     */
    @Override
    public void stopAllSounds() {
        for (Iterator<Sound> it = activeSounds.iterator(); it.hasNext(); ) {
            it.next().clip.stop();
            it.remove();
        }
    }

    /**
     * Plays given music based on its properties.
     *
     * @param music music to play
     */
    @Override
    public void playMusic(Music music) {
        if (!activeMusic.contains(music)) {
            activeMusic.add(music);
        }
        music.mediaPlayer.volumeProperty().bind(globalMusicVolumeProperty());
        music.mediaPlayer.play();
        music.isStopped = false;
    }

    /**
     * Pauses given music if it was previously started with {@link #playSound(Sound)}.
     * It can then be restarted by {@link #resumeMusic(Music)}.
     *
     * @param music music to pause
     */
    @Override
    public void pauseMusic(Music music) {
        if (activeMusic.contains(music))
            music.mediaPlayer.pause();
    }

    /**
     * Resumes previously paused {@link #pauseMusic(Music)} music.
     *
     * @param music music to resume
     */
    @Override
    public void resumeMusic(Music music) {
        if (activeMusic.contains(music))
            music.mediaPlayer.play();
    }

    /**
     * Stops currently playing music. It cannot be restarted
     * using {@link #resumeMusic(Music)}. The music object needs
     * to be started again by {@link #playMusic(Music)}.
     *
     * @param music music to stop
     */
    @Override
    public void stopMusic(Music music) {
        if (activeMusic.contains(music)) {
            activeMusic.remove(music);
            music.mediaPlayer.stop();
            music.isStopped = true;
        }
    }

    /**
     * Pauses all currently playing music. These can be
     * resumed using {@link #resumeAllMusic()}.
     */
    @Override
    public void pauseAllMusic() {
        activeMusic.forEach(music -> music.mediaPlayer.pause());
    }

    /**
     * Resumes all currently paused music.
     */
    @Override
    public void resumeAllMusic() {
        activeMusic.forEach(music -> music.mediaPlayer.play());
    }

    /**
     * Stops all currently playing music. The music cannot be restarted
     * by calling {@link #resumeAllMusic()}. Each music object will need
     * to be started by {@link #playMusic(Music)}.
     */
    @Override
    public void stopAllMusic() {
        log.finer("Stopping all music. Active music size: " + activeMusic.size());
        for (Iterator<Music> it = activeMusic.iterator(); it.hasNext(); ) {
            Music music = it.next();
            music.mediaPlayer.stop();
            music.isStopped = true;
            it.remove();
        }
    }

    @Override
    public void save(UserProfile profile) {
        log.finer("Saving data to profile");

        UserProfile.Bundle bundle = new UserProfile.Bundle("audio");
        bundle.put("musicVolume", getGlobalMusicVolume());
        bundle.put("soundVolume", getGlobalSoundVolume());

        bundle.log();
        profile.putBundle(bundle);
    }

    @Override
    public void load(UserProfile profile) {
        log.finer("Loading data from profile");
        UserProfile.Bundle bundle = profile.getBundle("audio");
        bundle.log();

        setGlobalMusicVolume(bundle.get("musicVolume"));
        setGlobalSoundVolume(bundle.get("soundVolume"));
    }
}
