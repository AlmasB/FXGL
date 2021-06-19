/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene3d

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.ObservableFloatArray
import javafx.geometry.Point3D
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.Mesh
import javafx.scene.shape.MeshView
import javafx.scene.shape.TriangleMesh
import java.util.stream.Collectors
import java.util.stream.IntStream

// TODO: cache meshes (check CylinderKey)

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
        const val DEFAULT_START_ANGLE = 0.0
    }

    /**
     * Convenient getter/setter for Point3D(translateX, translateY, translateZ).
     */
    var translation: Point3D
        get() = Point3D(translateX, translateY, translateZ)
        set(value) {
            translateX = value.x
            translateY = value.y
            translateZ = value.z
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

    // TODO: this is lazy-init, so we need to update it when the mesh changes
    val vertices: List<MeshVertex> by lazy {
        val triMesh = mesh as TriangleMesh

        val numVertices = triMesh.points.size() / 3

        IntStream.range(0, numVertices)
                .mapToObj { MeshVertex(triMesh.points, it*3) }
                .collect(Collectors.toUnmodifiableList())
    }

    fun setPhongMaterial(color: Color) {
        material = PhongMaterial(color)
    }

    class MeshVertex internal constructor(
            private val array: ObservableFloatArray,

            private val xIndex: Int
    ) {

        private val xProp = object : SimpleDoubleProperty(array[xIndex].toDouble()) {
            override fun invalidated() {
                array[xIndex] = x.toFloat()
            }
        }

        private val yProp = object : SimpleDoubleProperty(array[xIndex + 1].toDouble()) {
            override fun invalidated() {
                array[xIndex + 1] = y.toFloat()
            }
        }

        private val zProp = object : SimpleDoubleProperty(array[xIndex + 2].toDouble()) {
            override fun invalidated() {
                array[xIndex + 2] = z.toFloat()
            }
        }

        var x: Double
            get() = xProp.value
            set(value) { xProp.value = value }

        var y: Double
            get() = yProp.value
            set(value) { yProp.value = value }

        var z: Double
            get() = zProp.value
            set(value) { zProp.value = value }

        fun xProperty() = xProp
        fun yProperty() = yProp
        fun zProperty() = zProp
    }
}