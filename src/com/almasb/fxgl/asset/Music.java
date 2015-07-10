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
 * Represents a long-term audio. Use for background music.
 * Typically .mp3 files
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 * @version 1.0
 *
 */
public class Music {

    private final MediaPlayer mediaPlayer;

    /*package-private*/ Music(Media media) {
        mediaPlayer = new MediaPlayer(media);
    }

    /**
     * Plays the music file once only
     */
    public void playOnce() {
        mediaPlayer.setCycleCount(1);
        mediaPlayer.play();
    }

    /**
     * Resumes previously stopped or paused music.
     * If it was paused, it will continue from that moment.
     * If it was stopped, it will start from beginning
     */
    public void resume() {
        mediaPlayer.play();
    }

    /**
     * Pauses music playback
     */
    public void pause() {
        mediaPlayer.pause();
    }

    /**
     * Stops music playback
     */
    public void stop() {
        mediaPlayer.stop();
    }

    /**
     * Plays the music indefinitely
     */
    public void loop() {
        mediaPlayer.setCycleCount(Integer.MAX_VALUE);
        mediaPlayer.play();
    }
}
