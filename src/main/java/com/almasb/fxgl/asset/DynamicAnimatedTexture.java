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

import java.util.ArrayList;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;

/**
 * Represents a dynamic animated texture. It is similar to StaticAnimatedTexture,
 * but has animation channels, like WALK, RUN, IDLE, ATTACK, etc. which can
 * be set dynamically to alter the animation.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class DynamicAnimatedTexture extends Texture {

    private List<AnimationChannel> animationChannels = new ArrayList<>();
    private IntegerProperty frame = new SimpleIntegerProperty(0);
    private ChangeListener<Number> frameListener;

    private Timeline timeline = new Timeline();

    DynamicAnimatedTexture(Image image, AnimationChannel initialChannel, AnimationChannel... channels) {
        super(image);
        timeline.setCycleCount(Timeline.INDEFINITE);

        for (AnimationChannel c : channels)
            animationChannels.add(c);
        setAnimationChannel(initialChannel);
    }

    /**
     * Set animation channel. If animation channel wasn't registered
     * when creating instance of DynamicAnimatedTexture, this method
     * will throw IllegalArgumentException
     *
     * @param channel
     */
    public void setAnimationChannel(AnimationChannel channel) {
        if (!animationChannels.contains(channel)) {
            throw new IllegalArgumentException("Channel: [" + channel + "] is not registered for this texture.");
        }

        setFitWidth(channel.computeFrameWidth());
        setFitHeight(channel.computeFrameHeight());
        setViewport(channel.computeViewport(0));

        if (frameListener != null) {
            frame.removeListener(frameListener);
        }

        frameListener = (obs, old, newFrame) -> {
            setViewport(channel.computeViewport(newFrame.intValue()));
        };
        frame.addListener(frameListener);

        timeline.getKeyFrames().setAll(new KeyFrame(channel.duration(), new KeyValue(frame, channel.frames() - 1)));
        timeline.play();
    }
}
