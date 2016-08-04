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

package com.almasb.fxgl.texture;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.event.FXGLEvent;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxgl.time.UpdateEvent;
import com.almasb.fxgl.time.UpdateEventListener;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

/**
 * A texture which is statically animated, i.e.
 * loops through its frames constantly
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class StaticAnimatedTexture extends Texture implements UpdateEventListener {

    // TODO: can we replace with a single AnimationChannel?
    // Dynamic has similar code, we need to apply DRY
    private int currentFrame = 0;
    private double timePerAnimationFrame = 0;
    private LocalTimer animationTimer;

    private int maxFrames;
    private double frameW;

    /**
     * @param image    actual image
     * @param frames   number of frames in sprite sheet
     * @param duration duration of the animation
     */
    StaticAnimatedTexture(Image image, int frames, Duration duration) {
        super(image);

        frameW = image.getWidth() / frames;

        this.setFitWidth(frameW);
        this.setFitHeight(image.getHeight());

        this.setViewport(new Rectangle2D(0, 0, frameW, image.getHeight()));

        maxFrames = frames;
        timePerAnimationFrame = duration.toSeconds() / frames;

        animationTimer = FXGL.newLocalTimer();

        // TODO: clean listener
        FXGL.getMasterTimer().addUpdateListener(this);
    }

    @Override
    public void onUpdateEvent(UpdateEvent event) {
        if (animationTimer.elapsed(Duration.seconds(timePerAnimationFrame))) {

            setViewport(new Rectangle2D(currentFrame * frameW, 0, frameW, getFitHeight()));
            currentFrame++;

            if (currentFrame == maxFrames) {
                currentFrame = 0;
            }

            animationTimer.capture();
        }
    }
}
