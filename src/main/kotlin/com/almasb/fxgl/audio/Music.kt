/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

import javafx.beans.property.DoubleProperty
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer

/**
 * Represents a long-term audio in mp3 file.
 * Use for background (looping) music or recorded dialogues.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class Music(media: Media) {

    enum class Status {
        PAUSED, PLAYING, STOPPED
    }

    private val mediaPlayer: MediaPlayer

    internal var status = Status.STOPPED
        private set

    init {
        mediaPlayer = MediaPlayer(media)
    }

    // check current time + current count
    // cycle count may have been altered, hence >=
    internal fun reachedEnd() =
            mediaPlayer.currentTime == mediaPlayer.cycleDuration
                    && mediaPlayer.currentCount >= mediaPlayer.cycleCount

    internal fun start() {
        if (status == Status.STOPPED) {
            status = Status.PLAYING
            mediaPlayer.play()
        }
    }

    internal fun pause() {
        if (status == Status.PLAYING) {
            status = Status.PAUSED
            mediaPlayer.pause()
        }
    }

    internal fun resume() {
        if (status == Status.PAUSED) {
            status = Status.PLAYING
            mediaPlayer.play()
        }
    }

    internal fun stop() {
        status = Status.STOPPED
        mediaPlayer.stop()
    }

    internal fun bindVolume(volume: DoubleProperty) {
        mediaPlayer.volumeProperty().bind(volume)
    }

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
    var balance: Double
        get() = mediaPlayer.balance
        set(balance) {
            mediaPlayer.balance = balance
        }

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
    var rate: Double
        get() {
            return mediaPlayer.getRate()
        }
        set(rate) {
            mediaPlayer.setRate(rate)
        }

    /**
     * @return number of times the music to be played
     */
    /**
     * Set number of times the music will be played. Setting
     * to [Integer.MAX_VALUE] effectively loops the music.
     * Useful for background music.

     * @param count
     */
    var cycleCount: Int
        get() {
            return mediaPlayer.getCycleCount()
        }
        set(count) {
            mediaPlayer.setCycleCount(count)
        }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("Music [balance=")
        builder.append(balance)
        builder.append(", volume=")
        builder.append(mediaPlayer.getVolume())
        builder.append(", rate=")
        builder.append(rate)
        builder.append(", cycleCount=")
        builder.append(cycleCount)
        builder.append("]")
        return builder.toString()
    }
}
