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
        // TODO: fix rounding error
        // we should also take the last "quad" which may be smaller than chunk size
        val chunkSizeDouble = if (orientation == Orientation.HORIZONTAL)
            texture.image.height / numChunks
        else
            texture.image.width / numChunks

        val chunkSize = chunkSizeDouble.toInt().toDouble()



        for (i in 0 until numChunks) {
            val quad: Texture

            if (orientation == Orientation.HORIZONTAL) {
                quad = texture.subTexture(Rectangle2D(0.0, i * chunkSize, texture.image.width, chunkSize))
                quad.translateY = i * chunkSize
            } else {
                quad = texture.subTexture(Rectangle2D(i * chunkSize, 0.0, chunkSize, texture.image.height))
                quad.translateX = i * chunkSize
            }

            quads.add(quad)

            newView.children.add(quad)
        }
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