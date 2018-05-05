/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.animation


import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.components.ColorComponent
import com.almasb.fxgl.util.EmptyRunnable
import javafx.animation.Interpolator
import javafx.util.Duration
import java.util.*

/**
 * A convenient builder for standard (translate, rotate, scale) animations.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AnimationBuilder {

    var duration = Duration.seconds(1.0)
        private set

    var delay = Duration.ZERO
        private set

    var interpolator = Interpolator.LINEAR
        private set

    var times = 1
        private set

    var onFinished: Runnable = EmptyRunnable
        private set

    var isAutoReverse = false
        private set

    // guaranteed to be initialized before access by specific animation builder
    // see rotate(), scale(), translate(), etc. below
    internal lateinit var entities: List<Entity>
        private set

    fun duration(duration: Duration): AnimationBuilder {
        this.duration = duration
        return this
    }

    fun delay(delay: Duration): AnimationBuilder {
        this.delay = delay
        return this
    }

    fun repeat(times: Int): AnimationBuilder {
        this.times = times
        return this
    }

    fun repeatInfinitely(): AnimationBuilder {
        return repeat(Integer.MAX_VALUE)
    }

    fun onFinished(onFinished: Runnable): AnimationBuilder {
        this.onFinished = onFinished
        return this
    }

    fun interpolator(interpolator: Interpolator): AnimationBuilder {
        this.interpolator = interpolator
        return this
    }

    fun autoReverse(autoReverse: Boolean): AnimationBuilder {
        this.isAutoReverse = autoReverse
        return this
    }

    fun rotate(vararg entities: Entity): RotationAnimationBuilder {
        return rotate(Arrays.asList(*entities))
    }

    fun rotate(entities: List<Entity>): RotationAnimationBuilder {
        this.entities = entities
        return RotationAnimationBuilder(this)
    }

    fun translate(vararg entities: Entity): TranslationAnimationBuilder {
        return translate(Arrays.asList(*entities))
    }

    fun translate(entities: List<Entity>): TranslationAnimationBuilder {
        this.entities = entities
        return TranslationAnimationBuilder(this)
    }

    fun scale(vararg entities: Entity): ScaleAnimationBuilder {
        return scale(Arrays.asList(*entities))
    }

    fun scale(entities: List<Entity>): ScaleAnimationBuilder {
        this.entities = entities
        return ScaleAnimationBuilder(this)
    }

    fun color(vararg entities: Entity): ColorAnimationBuilder {
        return color(Arrays.asList(*entities))
    }

    fun color(entities: List<Entity>): ColorAnimationBuilder {
        this.entities = entities

        for (e in entities) {
            if (!e.hasComponent(ColorComponent::class.java)) {
                throw IllegalArgumentException("All entities must have ColorComponent")
            }
        }

        return ColorAnimationBuilder(this)
    }
}