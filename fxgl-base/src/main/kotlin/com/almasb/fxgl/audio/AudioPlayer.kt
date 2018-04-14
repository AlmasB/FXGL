/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.audio

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.asset.FXGLAssets
import com.almasb.fxgl.core.collection.UnorderedArray
import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.gameplay.notification.NotificationEvent
import com.almasb.fxgl.io.serialization.Bundle
import com.almasb.fxgl.saving.UserProfile
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Point2D

/**
 * General audio player service that supports playback of sound and music objects.
 * It can also control volume of both.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class AudioPlayer {

    private val log = Logger.get(javaClass)

    private val activeMusic = UnorderedArray<Music>()
    private val activeSounds = UnorderedArray<Sound>()

    private val globalMusicVolume = SimpleDoubleProperty(0.5)
    private val globalSoundVolume = SimpleDoubleProperty(0.5)

    init {
        globalMusicVolume.addListener { _, _, newVolume ->
            activeMusic.forEach { it.audio.setVolume(newVolume.toDouble()) }
        }

        globalSoundVolume.addListener { _, _, newVolume ->
            activeSounds.forEach { it.audio.setVolume(newVolume.toDouble()) }
        }
    }

    fun onUpdate(tpf: Double) {
        activeMusic.removeAll { it.isDisposed }
        activeSounds.removeAll { it.isDisposed }
    }

    fun globalMusicVolumeProperty(): DoubleProperty {
        return globalMusicVolume
    }

    fun globalSoundVolumeProperty(): DoubleProperty {
        return globalSoundVolume
    }

    fun onNotificationEvent(event: NotificationEvent) {
        playSound(FXGLAssets.SOUND_NOTIFICATION)
    }

    fun getGlobalMusicVolume(): Double {
        return globalMusicVolumeProperty().get()
    }

    /**
     * Set global music volume in the range [0..1],
     * where 0 = 0%, 1 = 100%.
     *
     * @param volume music volume
     */
    fun setGlobalMusicVolume(volume: Double) {
        globalMusicVolumeProperty().set(volume)
    }

    fun getGlobalSoundVolume(): Double {
        return globalSoundVolumeProperty().get()
    }

    /**
     * Set global sound volume in the range [0..1],
     * where 0 = 0%, 1 = 100%.
     *
     * @param volume sound volume
     */
    fun setGlobalSoundVolume(volume: Double) {
        globalSoundVolumeProperty().set(volume)
    }

    /**
     * @param assetName sound file name
     * *
     * @param soundPosition where sound is playing
     * *
     * @param earPosition where sound is heard
     * *
     * @param maxDistance how far the sound can be heard before it's "full" right or "full" left,
     * *                    i.e. if dist > maxDistance then sound balance is set to max (1.0) in that direction
     */
    fun playPositionalSound(assetName: String, soundPosition: Point2D, earPosition: Point2D, maxDistance: Double) {
        playPositionalSound(FXGL.getAssetLoader().loadSound(assetName), soundPosition, earPosition, maxDistance)
    }

    /**
     * @param sound sound
     * @param soundPosition where sound is playing
     * @param earPosition where sound is heard
     * @param maxDistance how far the sound can be heard before it's "full" right or "full" left,
     *                    i.e. if dist > maxDistance then sound balance is set to max (1.0) in that direction
     */
    fun playPositionalSound(sound: Sound, soundPosition: Point2D, earPosition: Point2D, maxDistance: Double) {
        val rawBalance = earPosition.distance(soundPosition) / maxDistance

        sound.balance = if (soundPosition.x > earPosition.x) {
            rawBalance
        } else {
            -rawBalance
        }

        playSound(sound)
    }

    /**
     * Convenience method to play the sound given its filename.
     *
     * @param assetName name of the sound file
     */
    fun playSound(assetName: String) {
        playSound(FXGL.getAssetLoader().loadSound(assetName))
    }

    /**
     * Plays given sound based on its properties.
     *
     * @param sound sound to play
     */
    fun playSound(sound: Sound) {
        if (!activeSounds.containsByIdentity(sound))
            activeSounds.add(sound)

        sound.audio.setVolume(getGlobalSoundVolume())
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

//    /**
//     * Stops playing all sounds.
//     */
//    fun stopAllSounds() {
//        log.debug("Stopping all sounds")
//
//        val it = activeSounds.iterator()
//        while (it.hasNext()) {
//            it.next().clip.stop()
//            it.remove()
//        }
//    }

    /**
     * @param bgmName name of the background music file to loop
     * @return the music object that is played in a loop
     */
    fun loopBGM(bgmName: String): Music {
        val music = FXGL.getAssetLoader().loadMusic(bgmName)
        music.cycleCount = Integer.MAX_VALUE
        music.audio.setLooping(true)
        playMusic(music)
        return music
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

        music.audio.setVolume(getGlobalMusicVolume())
        music.audio.play()
    }

    /**
     * Convenience method to play the music given its filename.
     *
     * @param assetName name of the music file
     */
    fun playMusic(assetName: String) {
        playMusic(FXGL.getAssetLoader().loadMusic(assetName))
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

//    /**
//     * Pauses all currently playing music.
//     * These can be resumed using [.resumeAllMusic].
//     */
//    fun pauseAllMusic() {
//        log.debug("Pausing all music")
//
//        activeMusic.forEach { it.pause() }
//    }
//
//    /**
//     * Resumes all currently paused music.
//     */
//    fun resumeAllMusic() {
//        log.debug("Resuming all music")
//
//        activeMusic.forEach { it.resume() }
//    }
//
//    /**
//     * Stops all currently playing music. The music cannot be restarted
//     * by calling [.resumeAllMusic]. Each music object will need
//     * to be started by [.playMusic].
//     */
//    fun stopAllMusic() {
//        log.debug("Stopping all music. Active music size: ${activeMusic.size}")
//
//        activeMusic.forEach { it.stop() }
//    }

    fun save(profile: UserProfile) {
        log.debug("Saving data to profile")

        val bundle = Bundle("audio")
        bundle.put("musicVolume", getGlobalMusicVolume())
        bundle.put("soundVolume", getGlobalSoundVolume())

        bundle.log()
        profile.putBundle(bundle)
    }

    fun load(profile: UserProfile) {
        log.debug("Loading data from profile")
        val bundle = profile.getBundle("audio")
        bundle.log()

        setGlobalMusicVolume(bundle.get<Double>("musicVolume"))
        setGlobalSoundVolume(bundle.get<Double>("soundVolume"))
    }
}