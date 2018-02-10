/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.effect

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.entity.Effect
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.view.EntityView
import com.almasb.fxgl.texture.Texture
import javafx.geometry.Rectangle2D
import javafx.scene.Node
import javafx.util.Duration
import java.util.ArrayList

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class WobbleEffect(duration: Duration) : Effect(duration) {

    private val quads = ArrayList<Texture>()
    private lateinit var originalView: Node

    override fun onStart(entity: Entity) {
        //originalView = entity.view

        val originalTexture = Texture(entity.view.snapshot(null, null))
        originalView = originalTexture

        // TODO: need to copy render layer
        val newView = EntityView()

        val numChunks = 8

        val chunkSize = originalTexture.image.width / numChunks

        println(chunkSize)

        for (i in 0 until numChunks) {
            val quad = originalTexture.subTexture(Rectangle2D(0.0, i * chunkSize, originalTexture.image.width, chunkSize))
            quad.translateY = i * chunkSize

            quads.add(quad)

            newView.addNode(quad)
        }

        entity.setView(newView)
    }

    override fun onUpdate(entity: Entity, tpf: Double) {
        for (i in quads.indices) {
            val t = quads.get(i)
            t.translateX = FXGLMath.sin((FXGL.getApp().tick + i / 0.5f).toDouble()) * 1.5
        }
    }

    override fun onEnd(entity: Entity) {
        entity.setView(originalView)
    }
}