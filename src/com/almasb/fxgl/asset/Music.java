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

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Represents a long-term audio in mp3 file. Use for
 * background (looping) music or recorded dialogues.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class Music {

    /*package-private*/ final MediaPlayer mediaPlayer;

    /*package-private*/ Music(Media media) {
        mediaPlayer = new MediaPlayer(media);
    }

    /**
     * The balance, or left-right setting, of the audio output. The range of
     * effective values is <code>[-1.0,&nbsp;1.0]</code> with <code>-1.0</code>
     * being full left, <code>0.0</code> center, and <code>1.0</code> full right.
     * The default value is <code>0.0</code>.
     *
     * @param balance
     */
    public void setBalance(double balance) {
        mediaPlayer.setBalance(balance);
    }

    /**
     *
     * @return balance of the audio output
     */
    public double getBalance() {
        return mediaPlayer.getBalance();
    }

    /**
     * The rate at which the media should be played. For example, a rate of
     * <code>1.0</code> plays the media at its normal (encoded) playback rate,
     * <code>2.0</code> plays back at twice the normal rate, etc. The currently
     * supported range of rates is <code>[0.0,&nbsp;8.0]</code>. The default
     * value is <code>1.0</code>.
     *
     * @param rate
     */
    public void setRate(double rate) {
        mediaPlayer.setRate(rate);
    }

    /**
     *
     * @return music rate
     */
    public double getRate() {
        return mediaPlayer.getRate();
    }

    /**
     * Set number of times the music will be played. Setting
     * to {@link Integer#MAX_VALUE} effectively loops the music.
     * Useful for background music.
     *
     * @param count
     */
    public void setCycleCount(int count) {
        mediaPlayer.setCycleCount(count);
    }

    /**
     *
     * @return number of times the music to be played
     */
    public double getCycleCount() {
        return mediaPlayer.getCycleCount();
    }
}
