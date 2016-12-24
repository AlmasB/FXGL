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

package com.almasb.fxgl.audio

import javafx.scene.media.AudioClip

/**
 * Represents a short sound in .wav file.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class Sound(internal val clip: AudioClip) {

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
    var balance: Double
        get() = clip.balance
        set(balance) {
            clip.balance = balance
        }

    /**
     * @return sound pan value
     */
    /**
     * The relative "center" of the clip. A pan value of 0.0 plays
     * the clip normally where a -1.0 pan shifts the clip entirely to the left
     * channel and 1.0 shifts entirely to the right channel. Unlike balance this
     * setting mixes both channels so neither channel loses data. Setting
     * pan on a mono clip has the same effect as setting balance, but with a
     * much higher cost in CPU overhead so this is not recommended for mono
     * clips.

     * @param pan sound pan
     */
    var pan: Double
        get() {
            return clip.getPan()
        }
        set(pan) {
            clip.setPan(pan)
        }

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
    var rate: Double
        get() {
            return clip.getRate()
        }
        set(rate) {
            clip.setRate(rate)
        }

    /**
     * @return number of times sound to be played
     */
    /**
     * The number of times the sound is to be played
     * in the range [1..Integer.MAX_VALUE]

     * @param count number of times to play
     */
    var cycleCount: Int
        get() {
            return clip.getCycleCount()
        }
        set(count) {
            clip.setCycleCount(count)
        }
}
