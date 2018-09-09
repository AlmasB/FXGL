/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.animation

import com.almasb.fxgl.animation.*
import com.almasb.fxgl.core.util.Consumer
import javafx.geometry.Point2D
import javafx.scene.shape.CubicCurve
import javafx.scene.shape.QuadCurve
import javafx.scene.shape.Shape

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TranslationAnimationBuilder(private val animationBuilder: EntityAnimationBuilder) {

    private var path: Shape? = null
    private var fromPoint = Point2D.ZERO
    private var toPoint = Point2D.ZERO

    fun alongPath(path: Shape): TranslationAnimationBuilder {
        this.path = path
        return this
    }

    fun from(start: Point2D): TranslationAnimationBuilder {
        fromPoint = start
        return this
    }

    fun to(end: Point2D): TranslationAnimationBuilder {
        toPoint = end
        return this
    }

    fun build(): Animation<*> {

        path?.let { curve ->
            when (curve) {
                is QuadCurve -> return makeAnim(AnimatedQuadBezierPoint2D(curve))
                is CubicCurve -> return makeAnim(AnimatedCubicBezierPoint2D(curve))
                else -> throw IllegalArgumentException("Unsupported path: $curve")
            }
        }

        return makeAnim(AnimatedPoint2D(fromPoint, toPoint))
    }

    private fun makeAnim(animValue: AnimatedValue<Point2D>): Animation<Point2D> {
        return animationBuilder.animationBuilder.build(
                animValue,
                Consumer { value -> animationBuilder.entities.forEach { it.position = value } }
        )
    }

    fun buildAndPlay(): Animation<*> {
        val anim = build()
        anim.start()
        return anim
    }
}