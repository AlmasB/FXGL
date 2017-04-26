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

package com.almasb.fxgl.service.impl.audio

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.audio.Music
import com.almasb.fxgl.audio.Sound
import com.almasb.fxgl.io.serialization.Bundle
import com.almasb.fxgl.service.AudioPlayer
import com.almasb.fxgl.saving.UserProfile
import com.google.inject.Inject
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import java.util.*

/**
 * FXGL provider of audio service.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGLAudioPlayer
@Inject
private constructor() : AudioPlayer {

    private val log = FXGL.getLogger(javaClass)

    override fun onUpdate(tpf: Double) {

        activeMusic.filter { it.reachedEnd() }
                .forEach {
                    log.debug("Stopping music: $it")
                    it.stop()
                }

        activeSounds.removeIf { !it.clip.isPlaying }
        activeMusic.removeIf { it.status == Music.Status.STOPPED }
    }

    /**
     * Contains sounds which are currently playing.
     */
    private val activeSounds = ArrayList<Sound>()

    /**
     * Contains music objects which are currently playing or paused.
     */
    private val activeMusic = ArrayList<Music>()

    private val globalMusicVolume = SimpleDoubleProperty(0.5)

    /**
     * @return global music volume property
     */
    override fun globalMusicVolumeProperty(): DoubleProperty {
        return globalMusicVolume
    }

    private val globalSoundVolume = SimpleDoubleProperty(0.5)

    /**
     * @return global sound volume property
     */
    override fun globalSoundVolumeProperty(): DoubleProperty {
        return globalSoundVolume
    }

    /**
     * Plays given sound based on its properties.

     * @param sound sound to play
     */
    override fun playSound(sound: Sound) {
        if (!activeSounds.contains(sound))
            activeSounds.add(sound)
        sound.clip.volumeProperty().bind(globalSoundVolumeProperty())
        sound.clip.play()
    }

    /**
     * Stops playing given sound.

     * @param sound sound to stop
     */
    override fun stopSound(sound: Sound) {
        activeSounds.remove(sound)
        sound.clip.stop()
    }

    /**
     * Stops playing all sounds.
     */
    override fun stopAllSounds() {
        log.debug("Stopping all sounds")

        val it = activeSounds.iterator()
        while (it.hasNext()) {
            it.next().clip.stop()
            it.remove()
        }
    }

    /**
     * Plays given music based on its properties.
     *
     * @param music music to play
     */
    override fun playMusic(music: Music) {
        log.debug("Playing music $music")

        if (!activeMusic.contains(music)) {
            activeMusic.add(music)
        } else {
            throw IllegalArgumentException("Attempted to play $music, which is already playing / paused")
        }

        music.bindVolume(globalMusicVolume)
        music.start()
    }

    /**
     * Pauses given music if it was previously started with [.playSound].
     * It can then be restarted by [.resumeMusic].

     * @param music music to pause
     */
    override fun pauseMusic(music: Music) {
        log.debug("Pausing music $music")

        if (activeMusic.contains(music))
            music.pause()
        else
            log.warning("Attempted to pause $music that is not managed by audio player. Managed music: $activeMusic")
    }

    /**
     * Resumes previously paused [.pauseMusic] music.

     * @param music music to resume
     */
    override fun resumeMusic(music: Music) {
        log.debug("Resuming music $music")

        if (activeMusic.contains(music))
            music.resume()
        else
            log.warning("Attempted to resume $music that is not managed by audio player. Managed music: $activeMusic")
    }

    /**
     * Stops currently playing music. It cannot be restarted
     * using [.resumeMusic]. The music object needs
     * to be started again by [.playMusic].

     * @param music music to stop
     */
    override fun stopMusic(music: Music) {
        log.debug("Stopping music $music")

        if (activeMusic.contains(music)) {
            music.stop()
            activeMusic.remove(music)
        } else {
            log.warning("Attempted to stop $music that is not managed by audio player. Managed music: $activeMusic")
        }
    }

    /**
     * Pauses all currently playing music.
     * These can be resumed using [.resumeAllMusic].
     */
    override fun pauseAllMusic() {
        log.debug("Pausing all music")

        activeMusic.forEach { it.pause() }
    }

    /**
     * Resumes all currently paused music.
     */
    override fun resumeAllMusic() {
        log.debug("Resuming all music")

        activeMusic.forEach { it.resume() }
    }

    /**
     * Stops all currently playing music. The music cannot be restarted
     * by calling [.resumeAllMusic]. Each music object will need
     * to be started by [.playMusic].
     */
    override fun stopAllMusic() {
        log.debug("Stopping all music. Active music size: ${activeMusic.size}")

        activeMusic.forEach { it.stop() }
    }

    override fun save(profile: UserProfile) {
        log.debug("Saving data to profile")

        val bundle = Bundle("audio")
        bundle.put("musicVolume", getGlobalMusicVolume())
        bundle.put("soundVolume", getGlobalSoundVolume())

        bundle.log()
        profile.putBundle(bundle)
    }

    override fun load(profile: UserProfile) {
        log.debug("Loading data from profile")
        val bundle = profile.getBundle("audio")
        bundle.log()

        setGlobalMusicVolume(bundle.get<Double>("musicVolume"))
        setGlobalSoundVolume(bundle.get<Double>("soundVolume"))
    }
}