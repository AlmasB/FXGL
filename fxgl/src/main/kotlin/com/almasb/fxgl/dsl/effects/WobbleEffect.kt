/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.effects

import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.dsl.components.Effect
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.texture.Texture
import javafx.geometry.Orientation
import javafx.geometry.Rectangle2D
import javafx.scene.Group
import javafx.util.Duration

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class WobbleEffect
@JvmOverloads constructor(
        val texture: Texture,
        duration: Duration,
        val radius: Int = 2,
        val numChunks: Int = 5,
        val orientation: Orientation = Orientation.HORIZONTAL
) : Effect(duration) {

    private val quads = arrayListOf<Texture>()

    private val newView = Group()

    private var tick = 0

    init {
        val chunkSize = getChunkSize()
        val endVal = getMaxDimension()
        var minVal = 0.0

        while (minVal < endVal) {
            val quad: Texture

            val maxVal = if (minVal + chunkSize > endVal)
                endVal
            else
                (minVal + chunkSize).toInt().toDouble()

            if (orientation == Orientation.HORIZONTAL) {
                quad = texture.subTexture(Rectangle2D(0.0, minVal, texture.image.width, maxVal - minVal))
                quad.translateY = minVal
            } else {
                quad = texture.subTexture(Rectangle2D(minVal, 0.0, maxVal - minVal, texture.image.height))
                quad.translateX = minVal
            }

            minVal = maxVal

            quads.add(quad)
            newView.children.add(quad)
        }
    }

    private fun getMaxDimension(): Double {
        return if (orientation == Orientation.HORIZONTAL)
            texture.image.height
        else
            texture.image.width
    }

    private fun getChunkSize(): Double {
        return if (orientation == Orientation.HORIZONTAL)
            texture.image.height / numChunks
        else
            texture.image.width / numChunks
    }

    override fun onStart(entity: Entity) {
        tick = 0

        entity.viewComponent.children.forEach { it.isVisible = false }

        entity.viewComponent.addChild(newView)
    }

    override fun onUpdate(entity: Entity, tpf: Double) {
        tick += 1

        quads.forEachIndexed { index, quad ->

            val value = FXGLMath.sin((tick + index / 0.5)) * radius

            if (orientation == Orientation.HORIZONTAL) {
                quad.translateX = value
            } else {
                quad.translateY = value
            }
        }
    }

    override fun onEnd(entity: Entity) {
        entity.viewComponent.removeChild(newView)

        entity.viewComponent.children.forEach { it.isVisible = true }
    }
}