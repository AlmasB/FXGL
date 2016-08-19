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
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxgl.time.UpdateEvent;
import com.almasb.fxgl.time.UpdateEventListener;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a dynamic animated texture. It is similar to StaticAnimatedTexture,
 * but has animation channels, like WALK, RUN, IDLE, ATTACK, etc. which can
 * be set dynamically to alter the animation.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class DynamicAnimatedTexture extends Texture implements UpdateEventListener {

    private List<AnimationChannel> animationChannels = new ArrayList<>();
    private AnimationChannel defaultChannel;
    private AnimationChannel currentChannel;

    private int currentFrame = 0;
    private double timePerAnimationFrame = 0;
    private LocalTimer animationTimer;

    private Runnable onAnimationEnd;

    DynamicAnimatedTexture(Image image, AnimationChannel initialChannel, AnimationChannel... channels) {
        super(image);
        this.defaultChannel = initialChannel;

        Collections.addAll(animationChannels, channels);
        setAnimationChannel(initialChannel);

        animationTimer = FXGL.newLocalTimer();

        // TODO: this listener needs to be removed somehow,
        // possibly via dispose as this is also an asset
        FXGL.getMasterTimer().addUpdateListener(this);
    }

    /**
     * Set animation channel. If animation channel wasn't registered
     * when creating instance of DynamicAnimatedTexture, this method
     * will throw IllegalArgumentException.
     *
     * @param channel animation channel
     */
    public void setAnimationChannel(AnimationChannel channel) {
        setAnimationChannel(channel, () -> {});
    }

    /**
     * Set animation channel. If animation channel wasn't registered
     * when creating instance of DynamicAnimatedTexture, this method
     * will throw IllegalArgumentException.
     *
     * @param channel animation channel
     * @param onAnimationEnd callback run when animation channel ends
     */
    public void setAnimationChannel(AnimationChannel channel, Runnable onAnimationEnd) {
        if (!animationChannels.contains(channel)) {
            throw new IllegalArgumentException("Channel: [" + channel + "] is not registered for this texture.");
        }

        if (currentChannel == channel && channel != defaultChannel)
            return;

        this.onAnimationEnd = onAnimationEnd;
        currentChannel = channel;

        currentFrame = 0;
        timePerAnimationFrame = channel.computeFrameTime();

        setFitWidth(channel.computeFrameWidth());
        setFitHeight(channel.computeFrameHeight());
        setViewport(channel.computeViewport(0));
    }

    /**
     * @return current channel
     */
    public AnimationChannel getCurrentChannel() {
        return currentChannel;
    }

    @Override
    public void onUpdateEvent(UpdateEvent event) {
        if (animationTimer.elapsed(Duration.seconds(timePerAnimationFrame))) {

            setViewport(currentChannel.computeViewport(currentFrame));
            currentFrame++;

            if (currentFrame == currentChannel.frames()) {
                onAnimationEnd.run();
                setAnimationChannel(defaultChannel);
            }

            animationTimer.capture();
        }
    }
}
