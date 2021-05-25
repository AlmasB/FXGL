/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene3d

import javafx.scene.shape.Mesh
import javafx.scene.shape.TriangleMesh

/**
 * Note: adapted from reference [javafx.scene.shape.Box] implementation.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Cuboid
@JvmOverloads constructor(
        width: Double = DEFAULT_SIZE,
        height: Double = DEFAULT_SIZE,
        depth: Double = DEFAULT_SIZE
) : CustomShape3D() {

    private val widthProp = newDoubleProperty(width)
    private val heightProp = newDoubleProperty(height)
    private val depthProp = newDoubleProperty(depth)

    var width: Double
        get() = widthProp.value
        set(value) { widthProp.value = value }

    var height: Double
        get() = heightProp.value
        set(value) { heightProp.value = value }

    var depth: Double
        get() = depthProp.value
        set(value) { depthProp.value = value }

    fun widthProperty() = widthProp
    fun heightProperty() = heightProp
    fun depthProperty() = depthProp

    init {
        updateMesh()
    }

    override fun createMesh(): Mesh {
        // adapted from Box::createMesh

        val w = width.toFloat()
        val h = height.toFloat()
        val d = depth.toFloat()

        // NOTE: still create mesh for degenerated box
        val hw: Float = w / 2f
        val hh: Float = h / 2f
        val hd: Float = d / 2f

        val points = floatArrayOf(
                -hw, -hh, -hd,
                hw, -hh, -hd,
                hw, hh, -hd,
                -hw, hh, -hd,
                -hw, -hh, hd,
                hw, -hh, hd,
                hw, hh, hd,
                -hw, hh, hd
        )

        val tPoints = floatArrayOf(0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f)

        // Specifies hard edges.
        val smoothing = intArrayOf(
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
        )

        val faces = intArrayOf(
                0, 0, 2, 2, 1, 1,
                2, 2, 0, 0, 3, 3,
                1, 0, 6, 2, 5, 1,
                6, 2, 1, 0, 2, 3,
                5, 0, 7, 2, 4, 1,
                7, 2, 5, 0, 6, 3,
                4, 0, 3, 2, 0, 1,
                3, 2, 4, 0, 7, 3,
                3, 0, 6, 2, 2, 1,
                6, 2, 3, 0, 7, 3,
                4, 0, 1, 2, 5, 1,
                1, 2, 4, 0, 0, 3
        )

        val m = TriangleMesh()
        m.points.setAll(*points)
        m.texCoords.setAll(*tPoints)
        m.faces.setAll(*faces)
        m.faceSmoothingGroups.setAll(*smoothing)

        return m
    }
}