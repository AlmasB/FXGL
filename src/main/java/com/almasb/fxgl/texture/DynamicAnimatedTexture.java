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
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;

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
public final class DynamicAnimatedTexture extends Texture {

    private List<AnimationChannel> animationChannels = new ArrayList<>();
    private AnimationChannel defaultChannel;
    private AnimationChannel currentChannel;

    private IntegerProperty frame = new SimpleIntegerProperty(0);
    private ChangeListener<Number> frameListener;

    private Timeline timeline = new Timeline();

    DynamicAnimatedTexture(Image image, AnimationChannel initialChannel, AnimationChannel... channels) {
        super(image);
        this.defaultChannel = initialChannel;
        timeline.setCycleCount(Timeline.INDEFINITE);

        Collections.addAll(animationChannels, channels);
        setAnimationChannel(initialChannel);

        FXGL.getEventBus().addEventHandler(FXGLEvent.PAUSE, e -> {
            timeline.pause();
        });

        FXGL.getEventBus().addEventHandler(FXGLEvent.RESUME, e -> {
            timeline.play();
        });
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

        if (currentChannel == channel)
            return;

        currentChannel = channel;
        timeline.setCycleCount(currentChannel == defaultChannel ? Timeline.INDEFINITE : 1);
        timeline.setOnFinished(currentChannel == defaultChannel ? null : e -> {
            onAnimationEnd.run();
            setAnimationChannel(defaultChannel);
        });

        setFitWidth(channel.computeFrameWidth());
        setFitHeight(channel.computeFrameHeight());
        setViewport(channel.computeViewport(0));

        if (frameListener != null) {
            frame.removeListener(frameListener);
        }

        frame.set(0);
        frameListener = (obs, old, newFrame) -> setViewport(channel.computeViewport(newFrame.intValue()));
        frame.addListener(frameListener);

        timeline.getKeyFrames().setAll(new KeyFrame(channel.duration(), new KeyValue(frame, channel.frames() - 1)));
        timeline.playFromStart();
    }

    /**
     * @return current channel
     */
    public AnimationChannel getCurrentChannel() {
        return currentChannel;
    }
}
