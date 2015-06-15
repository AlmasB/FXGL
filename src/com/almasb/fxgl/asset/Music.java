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

    public void playOnce() {
        mediaPlayer.setCycleCount(1);
        mediaPlayer.play();
    }

    public void resume() {
        mediaPlayer.play();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void stop() {
        mediaPlayer.stop();
    }

    public void loop() {
        mediaPlayer.setCycleCount(Integer.MAX_VALUE);
        mediaPlayer.play();
    }
}
