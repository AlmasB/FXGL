/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene3d

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.shape.Mesh
import javafx.scene.shape.MeshView

// TODO: cache meshes (check CylinderKey)
// TODO: vertices by lazy { MeshVertex list }
// TODO: diffuseColor

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class CustomShape3D : MeshView() {

    companion object {

        // these consts provide a unified unit size for all custom 3D shapes
        const val DEFAULT_NUM_DIVISIONS = 64
        const val DEFAULT_SIZE = 2.0
        const val DEFAULT_RADIUS = 1.0
    }

    protected fun updateMesh() {
        mesh = createMesh()
    }

    protected abstract fun createMesh(): Mesh

    protected fun newDoubleProperty(value: Double) = object : SimpleDoubleProperty(value) {
        override fun invalidated() {
            updateMesh()
        }
    }

    protected fun newIntProperty(value: Int) = object : SimpleIntegerProperty(value) {
        override fun invalidated() {
            updateMesh()
        }
    }
}