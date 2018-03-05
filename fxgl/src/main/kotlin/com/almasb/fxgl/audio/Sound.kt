/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.audio

import com.almasb.fxgl.core.Disposable
import com.gluonhq.charm.down.plugins.audio.Audio

/**
 * Represents a short sound in .wav file.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class Sound(internal val audio: Audio): Disposable {

    internal var isDisposed = false

    /**
     * @return balance of the audio output
     */
    /**
     * The balance, or left-right setting, of the audio output. The range of
     * effective values is `[-1.0,&nbsp;1.0]` with `-1.0`
     * being full left, `0.0` center, and `1.0` full right.
     * The default value is `0.0`.

     * @param balance sound balance
     */
    var balance: Double = 0.0

    /**
     * @return sound rate
     */
    /**
     * The relative rate at which the clip is played. Valid range is 0.125
     * (1/8 speed) to 8.0 (8x speed); values outside this range are clamped
     * internally. Normal playback for a clip is 1.0; any other rate will affect
     * pitch and duration accordingly.

     * @param rate sound rate
     */
    var rate: Double = 1.0

    /**
     * @return number of times sound to be played
     */
    /**
     * The number of times the sound is to be played
     * in the range [1..Integer.MAX_VALUE]

     * @param count number of times to play
     */
    var cycleCount: Int = 1

    override fun dispose() {
        isDisposed = true
    }
}
