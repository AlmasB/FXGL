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

import javafx.scene.media.AudioClip;

/**
 * Represents a short sound in .wav file.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class Sound {

    final AudioClip clip;
    // boolean isStopped = false;

    Sound(AudioClip clip) {
        this.clip = clip;
    }

    /**
     * The balance, or left-right setting, of the audio output. The range of
     * effective values is <code>[-1.0,&nbsp;1.0]</code> with <code>-1.0</code>
     * being full left, <code>0.0</code> center, and <code>1.0</code> full right.
     * The default value is <code>0.0</code>.
     *
     * @param balance sound balance
     */
    public void setBalance(double balance) {
        clip.setBalance(balance);
    }

    /**
     * @return balance of the audio output
     */
    public double getBalance() {
        return clip.getBalance();
    }

    /**
     * The relative "center" of the clip. A pan value of 0.0 plays
     * the clip normally where a -1.0 pan shifts the clip entirely to the left
     * channel and 1.0 shifts entirely to the right channel. Unlike balance this
     * setting mixes both channels so neither channel loses data. Setting
     * pan on a mono clip has the same effect as setting balance, but with a
     * much higher cost in CPU overhead so this is not recommended for mono
     * clips.
     *
     * @param pan sound pan
     */
    public void setPan(double pan) {
        clip.setPan(pan);
    }

    /**
     * @return sound pan value
     */
    public double getPan() {
        return clip.getPan();
    }

    /**
     * The relative rate at which the clip is played. Valid range is 0.125
     * (1/8 speed) to 8.0 (8x speed); values outside this range are clamped
     * internally. Normal playback for a clip is 1.0; any other rate will affect
     * pitch and duration accordingly.
     *
     * @param rate sound rate
     */
    public void setRate(double rate) {
        clip.setRate(rate);
    }

    /**
     * @return sound rate
     */
    public double getRate() {
        return clip.getRate();
    }

    /**
     * The number of times the sound is to be played
     * in the range [1..Integer.MAX_VALUE]
     *
     * @param count number of times to play
     */
    public void setCycleCount(int count) {
        clip.setCycleCount(count);
    }

    /**
     * @return number of times sound to be played
     */
    public int getCycleCount() {
        return clip.getCycleCount();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Sound [sourceURL=");
        builder.append(clip.getSource());
        builder.append(", balance=");
        builder.append(getBalance());
        builder.append(", volume=");
        builder.append(clip.getVolume());
        builder.append(", pan=");
        builder.append(getPan());
        builder.append(", rate=");
        builder.append(getRate());
        builder.append(", cycleCount=");
        builder.append(getCycleCount());
        builder.append("]");
        return builder.toString();
    }
}
