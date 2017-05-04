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

package com.almasb.fxgl.texture;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.listener.StateListener;
import com.almasb.fxgl.time.LocalTimer;
import javafx.scene.image.Image;
import javafx.util.Duration;

/**
 * Represents an animated texture.
 * Animation channels, like WALK, RUN, IDLE, ATTACK, etc. can
 * be set dynamically to alter the animation.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class AnimatedTexture extends Texture implements StateListener {

    private AnimationChannel defaultChannel;
    private AnimationChannel currentChannel;

    private int currentFrame = 0;
    private double timePerAnimationFrame = 0;
    private LocalTimer animationTimer;

    private Runnable onAnimationEnd;

    AnimatedTexture(Image image, AnimationChannel initialChannel) {
        super(image);
        this.defaultChannel = initialChannel;

        animationTimer = FXGL.newLocalTimer();

        setAnimationChannel(initialChannel);

        FXGL.getApp().addPlayStateListener(this);
    }

    /**
     * Set animation channel.
     *
     * @param channel animation channel
     */
    public void setAnimationChannel(AnimationChannel channel) {
        setAnimationChannel(channel, () -> {});
    }

    /**
     * Set animation channel with a callback to run when the channel ends.
     *
     * @param channel animation channel
     * @param onAnimationEnd callback run when animation channel ends
     */
    public void setAnimationChannel(AnimationChannel channel, Runnable onAnimationEnd) {
        if (currentChannel == channel && channel != defaultChannel)
            return;

        this.onAnimationEnd = onAnimationEnd;
        currentChannel = channel;

        currentFrame = 0;
        timePerAnimationFrame = channel.computeFrameTime();

        setFitWidth(channel.computeFrameWidth());
        setFitHeight(channel.computeFrameHeight());
        setViewport(channel.computeViewport(0));

        animationTimer.capture();
    }

    /**
     * @return current channel
     */
    public AnimationChannel getCurrentChannel() {
        return currentChannel;
    }

    @Override
    public void onUpdate(double tpf) {
        //System.out.println(currentFrame);
        if (getScene() == null)
            return;

        if (animationTimer.elapsed(Duration.seconds(timePerAnimationFrame))) {

            currentFrame++;

            if (currentFrame == currentChannel.frames()) {
                onAnimationEnd.run();
                setAnimationChannel(defaultChannel);

                return;
            }

            setViewport(currentChannel.computeViewport(currentFrame));

            animationTimer.capture();
        }
    }

    public void reset() {
        currentFrame = 0;
    }

    public void resetTimer() {
        animationTimer.capture();
    }

    @Override
    public void dispose() {
        super.dispose();

        FXGL.getApp().removePlayStateListener(this);
    }
}
