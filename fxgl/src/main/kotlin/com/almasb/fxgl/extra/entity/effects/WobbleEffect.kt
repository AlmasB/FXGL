/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.extra.entity.effects

import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.entity.Effect
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.view.EntityView
import com.almasb.fxgl.texture.Texture
import javafx.geometry.Orientation
import javafx.geometry.Rectangle2D
import javafx.scene.SnapshotParameters
import javafx.scene.paint.Color
import javafx.util.Duration
import java.util.*

/**
 * Note: this effect destroys the original entity view (along with its reference) and only records
 * the view snapshot.
 * So the effect can't be applied to anything animated.
 *
 * https://github.com/AlmasB/FXGL/issues/483
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class WobbleEffect
@JvmOverloads constructor(
        duration: Duration,
        val radius: Int = 2,
        val numChunks: Int = 5,
        val orientation: Orientation = Orientation.HORIZONTAL
) : Effect(duration) {

    private val quads = ArrayList<Texture>()
    private var originalTexture: Texture? = null

    private var tick = 0

    override fun onStart(entity: Entity) {
        tick = 0

        if (originalTexture == null) {
            val params = SnapshotParameters()
            params.fill = Color.TRANSPARENT

            originalTexture = Texture(entity.view.snapshot(params, null))
        }

        val newView = EntityView()

        val chunkSize = (if (orientation == Orientation.HORIZONTAL)
            originalTexture!!.image.height / numChunks
        else
            originalTexture!!.image.width / numChunks).toInt().toDouble()



        for (i in 0 until numChunks) {

            if (orientation == Orientation.HORIZONTAL) {
                val quad = originalTexture!!.subTexture(Rectangle2D(0.0, i * chunkSize, originalTexture!!.image.width, chunkSize))


                quad.translateY = i * chunkSize

                quads.add(quad)

                newView.addNode(quad)
            } else {
                val quad = originalTexture!!.subTexture(Rectangle2D(i * chunkSize, 0.0, chunkSize, originalTexture!!.image.height))


                quad.translateX = i * chunkSize

                quads.add(quad)

                newView.addNode(quad)
            }
        }

        entity.setView(newView)
    }

    override fun onUpdate(entity: Entity, tpf: Double) {
        tick += 1

        quads.forEachIndexed { index, quad ->
            if (orientation == Orientation.HORIZONTAL) {
                quad.translateX = FXGLMath.sin((tick + index / 0.5)) * radius
            } else {
                quad.translateY = FXGLMath.sin((tick + index / 0.5)) * radius
            }
        }
    }

    override fun onEnd(entity: Entity) {
        entity.setView(originalTexture)
    }
}