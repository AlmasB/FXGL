/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.audio

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.Inject
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.core.collection.UnorderedArray
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.sslogger.Logger
import javafx.beans.property.DoubleProperty

/**
 * General audio player service that supports playback of sound and music objects.
 * It can also control volume of both.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class AudioPlayer : EngineService {

    private val log = Logger.get(javaClass)

    private val activeMusic = UnorderedArray<Music>()
    private val activeSounds = UnorderedArray<Sound>()

    @Inject("globalMusicVolumeProperty")
    private lateinit var musicVolume: DoubleProperty

    @Inject("globalSoundVolumeProperty")
    private lateinit var soundVolume: DoubleProperty

    override fun onMainLoopStarting() {
        musicVolume.addListener { _, _, newVolume ->
            activeMusic.forEach { it.audio.setVolume(newVolume.toDouble()) }
        }

        soundVolume.addListener { _, _, newVolume ->
            activeSounds.forEach { it.audio.setVolume(newVolume.toDouble()) }
        }
    }

    override fun onGameReady(vars: PropertyMap) {
    }

    override fun onExit() {
    }

    override fun write(bundle: Bundle) {
    }

    override fun read(bundle: Bundle) {
    }

    override fun onUpdate(tpf: Double) {
        activeMusic.removeAll { it.isDisposed }
        activeSounds.removeAll { it.isDisposed }
    }

    /**
     * Plays given sound based on its properties.
     *
     * @param sound sound to play
     */
    fun playSound(sound: Sound) {
        if (!activeSounds.containsByIdentity(sound))
            activeSounds.add(sound)

        sound.audio.setVolume(soundVolume.value)
        sound.audio.play()
    }

    /**
     * Stops playing given sound.
     * 
     * @param sound sound to stop
     */
    fun stopSound(sound: Sound) {
        sound.audio.stop()
    }

    /**
     * Plays given music based on its properties.
     *
     * @param music music to play
     */
    fun playMusic(music: Music) {
        log.debug("Playing music $music")

        if (!activeMusic.containsByIdentity(music)) {
            activeMusic.add(music)
        }

        music.audio.setVolume(musicVolume.value)
        music.audio.play()
    }

    /**
     * Pauses given music if it was previously started with [.playSound].
     * It can then be restarted by [.resumeMusic].

     * @param music music to pause
     */
    fun pauseMusic(music: Music) {
        log.debug("Pausing music $music")

        music.audio.pause()
    }

    /**
     * Resumes previously paused [.pauseMusic] music.
     *
     * @param music music to resume
     */
    fun resumeMusic(music: Music) {
        log.debug("Resuming music $music")

        music.audio.play()
    }

    /**
     * Stops currently playing music. It cannot be restarted
     * using [.resumeMusic]. The music object needs
     * to be started again by [.playMusic].

     * @param music music to stop
     */
    fun stopMusic(music: Music) {
        log.debug("Stopping music $music")

        music.audio.stop()
    }

    fun loopMusic(music: Music) {
        music.audio.setLooping(true)
        playMusic(music)
    }
}