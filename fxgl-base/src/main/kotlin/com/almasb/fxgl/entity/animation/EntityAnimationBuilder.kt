/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.animation


import com.almasb.fxgl.animation.AnimationBuilder
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.components.ColorComponent
import javafx.animation.Interpolator
import javafx.util.Duration

/**
 * A convenient builder for standard (translate, rotate, scale) animations.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EntityAnimationBuilder {

    val animationBuilder = AnimationBuilder()

    // TODO: provide these directly to sub animation builders

    // guaranteed to be initialized before access by specific animation builder
    // see rotate(), scale(), translate(), etc. below
    internal lateinit var entities: List<Entity>
        private set

    fun duration(duration: Duration): EntityAnimationBuilder {
        animationBuilder.duration(duration)
        return this
    }

    fun delay(delay: Duration): EntityAnimationBuilder {
        animationBuilder.delay(delay)
        return this
    }

    fun interpolator(interpolator: Interpolator): EntityAnimationBuilder {
        animationBuilder.interpolator(interpolator)
        return this
    }

    fun repeat(times: Int): EntityAnimationBuilder {
        animationBuilder.repeat(times)
        return this
    }

    fun repeatInfinitely(): EntityAnimationBuilder {
        return repeat(Integer.MAX_VALUE)
    }

    fun onFinished(onFinished: Runnable): EntityAnimationBuilder {
        animationBuilder.onFinished(onFinished)
        return this
    }

    fun autoReverse(autoReverse: Boolean): EntityAnimationBuilder {
        animationBuilder.autoReverse(autoReverse)
        return this
    }

    fun rotate(vararg entities: Entity): RotationAnimationBuilder {
        return rotate(entities.toList())
    }

    fun rotate(entities: List<Entity>): RotationAnimationBuilder {
        this.entities = entities
        return RotationAnimationBuilder(this)
    }

    fun translate(vararg entities: Entity): TranslationAnimationBuilder {
        return translate(entities.toList())
    }

    fun translate(entities: List<Entity>): TranslationAnimationBuilder {
        this.entities = entities
        return TranslationAnimationBuilder(this)
    }

    fun scale(vararg entities: Entity): ScaleAnimationBuilder {
        return scale(entities.toList())
    }

    fun scale(entities: List<Entity>): ScaleAnimationBuilder {
        this.entities = entities
        return ScaleAnimationBuilder(this)
    }

    fun color(vararg entities: Entity): ColorAnimationBuilder {
        return color(entities.toList())
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