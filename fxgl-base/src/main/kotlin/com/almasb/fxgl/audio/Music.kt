/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.audio

import com.almasb.fxgl.core.Disposable
import com.gluonhq.charm.down.plugins.audio.Audio

/**
 * Represents a long-term audio in mp3 file.
 * Use for background (looping) music or recorded dialogues.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class Music(internal val audio: Audio) : Disposable {

    internal var isDisposed = false

    /**
     * @return balance of the audio output
     */
    /**
     * The balance, or left-right setting, of the audio output. The range of
     * effective values is `[-1.0,&nbsp;1.0]` with `-1.0`
     * being full left, `0.0` center, and `1.0` full right.
     * The default value is `0.0`.

     * @param balance
     */
    var balance = 0.0

    /**
     * @return music rate
     */
    /**
     * The rate at which the media should be played. For example, a rate of
     * `1.0` plays the media at its normal (encoded) playback rate,
     * `2.0` plays back at twice the normal rate, etc. The currently
     * supported range of rates is `[0.0,&nbsp;8.0]`. The default
     * value is `1.0`.

     * @param rate
     */
    var rate = 1.0

    /**
     * @return number of times the music to be played
     */
    /**
     * Set number of times the music will be played. Setting
     * to [Integer.MAX_VALUE] effectively loops the music.
     * Useful for background music.

     * @param count
     */
    var cycleCount = 1

    override fun dispose() {
        isDisposed = true
    }
}
