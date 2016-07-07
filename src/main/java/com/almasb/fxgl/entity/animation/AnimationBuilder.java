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

package com.almasb.fxgl.entity.animation;

import com.almasb.fxgl.entity.GameEntity;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class AnimationBuilder {

    private Duration duration = Duration.seconds(1);
    private Duration delay = Duration.ZERO;
    private int times = 1;
    private List<GameEntity> entities = Collections.emptyList();

    Duration getDelay() {
        return delay;
    }

    Duration getDuration() {
        return duration;
    }

    int getTimes() {
        return times;
    }

    List<GameEntity> getEntities() {
        return entities;
    }

    public AnimationBuilder duration(Duration duration) {
        this.duration = duration;
        return this;
    }

    public AnimationBuilder delay(Duration delay) {
        this.delay = delay;
        return this;
    }

    public AnimationBuilder repeat(int times) {
        this.times = times;
        return this;
    }

    public RotationAnimationBuilder rotate(GameEntity... entities) {
        return rotate(Arrays.asList(entities));
    }

    public RotationAnimationBuilder rotate(List<GameEntity> entities) {
        this.entities = entities;
        return new RotationAnimationBuilder(this);
    }
}