/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.animation;

import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.ColorComponent;
import javafx.animation.Interpolator;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A convenient builder for standard (translate, rotate, scale) animations.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class AnimationBuilder {

    private Duration duration = Duration.seconds(1);
    private Duration delay = Duration.ZERO;
    private Interpolator interpolator = Interpolator.LINEAR;
    private int times = 1;
    private List<GameEntity> entities = new ArrayList<>();

    Duration getDelay() {
        return delay;
    }

    Duration getDuration() {
        return duration;
    }

    int getTimes() {
        return times;
    }

    Interpolator getInterpolator() {
        return interpolator;
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

    public AnimationBuilder interpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }

    public RotationAnimationBuilder rotate(GameEntity... entities) {
        return rotate(Arrays.asList(entities));
    }

    public RotationAnimationBuilder rotate(List<GameEntity> entities) {
        this.entities.addAll(entities);
        return new RotationAnimationBuilder(this);
    }

    public TranslationAnimationBuilder translate(GameEntity... entities) {
        Collections.addAll(this.entities, entities);

        return new TranslationAnimationBuilder(this);
    }

    public ScaleAnimationBuilder scale(GameEntity... entities) {
        Collections.addAll(this.entities, entities);

        return new ScaleAnimationBuilder(this);
    }

    public ColorAnimationBuilder color(GameEntity... entities) {
        Collections.addAll(this.entities, entities);

        boolean dontHaveColor = this.entities.stream().anyMatch(e -> !e.hasComponent(ColorComponent.class));

        if (dontHaveColor) {
            throw new IllegalArgumentException("All entities must have ColorComponent");
        }

        return new ColorAnimationBuilder(this);
    }
}