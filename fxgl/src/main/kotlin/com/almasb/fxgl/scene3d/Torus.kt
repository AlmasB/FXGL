/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene3d

import com.almasb.fxgl.core.math.FXGLMath.*
import javafx.scene.shape.Mesh
import javafx.scene.shape.TriangleMesh

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Torus
@JvmOverloads constructor(
        radius: Double = 0.6,
        tubeRadius: Double = radius * 2.0 / 3.0,
        numDivisions: Int = DEFAULT_NUM_DIVISIONS
) : CustomShape3D() {

    private val radiusProp = newDoubleProperty(radius)
    private val tubeRadiusProp = newDoubleProperty(tubeRadius)
    private val numDivisionsProp = newIntProperty(if (numDivisions < 3) 3 else numDivisions)

    var radius: Double
        get() = radiusProp.value
        set(value) { radiusProp.value = value }

    var tubeRadius: Double
        get() = tubeRadiusProp.value
        set(value) { tubeRadiusProp.value = value }

    var numDivisions: Int
        get() = numDivisionsProp.value
        set(value) { numDivisionsProp.value = value }

    fun radiusProperty() = radiusProp
    fun tubeRadiusProperty() = tubeRadiusProp
    fun numDivisionsProperty() = numDivisionsProp

    init {
        updateMesh()
    }

    // adapted from https://github.com/FXyz/FXyz/blob/master/FXyz-Core/src/main/java/org/fxyz3d/shapes/primitives/TorusMesh.java
    override fun createMesh(): Mesh {

        val radiusDivisions = numDivisions
        val tubeDivisions = numDivisions
        val R = radius.toFloat()
        val tR = tubeRadius.toFloat()
        val tubeStartAngle = 0f
        val xOffset = 0f
        val yOffset = 0f
        val zOffset = 1f

        val numVerts = tubeDivisions * radiusDivisions
        val faceCount = numVerts * 2
        val points = FloatArray(numVerts * 3)
        val tPoints = FloatArray(numVerts * 2)
        val faces = IntArray(faceCount * 6)

        var pPos = 0
        var tPos = 0

        val tubeFraction = 1.0f / tubeDivisions
        val radiusFraction = 1.0f / radiusDivisions

        // create points
        // create tPoints
        for (tubeIndex in 0 until tubeDivisions) {
            val radian = tubeStartAngle + tubeFraction * tubeIndex * PI2
            for (radiusIndex in 0 until radiusDivisions) {
                val localRadian = radiusFraction * radiusIndex * PI2
                points[pPos + 0] = (R + tR * cosF(radian)) * (cosF(localRadian) + xOffset)
                points[pPos + 1] = (R + tR * cosF(radian)) * (sinF(localRadian) + yOffset)
                points[pPos + 2] = tR * sinF(radian) * zOffset
                pPos += 3

                val r = if (radiusIndex < tubeDivisions) tubeFraction * radiusIndex * PI2 else 0.0
                tPoints[tPos + 0] = sinF(r) * 0.5f + 0.5f
                tPoints[tPos + 1] = cosF(r) * 0.5f + 0.5f
                tPos += 2
            }
        }

        var fIndex = 0

        //create faces
        for (point in 0 until tubeDivisions) {
            for (crossSection in 0 until radiusDivisions) {
                val p0 = point * radiusDivisions + crossSection
                var p1 = if (p0 >= 0) p0 + 1 else p0 - radiusDivisions
                p1 = if (p1 % radiusDivisions != 0) p0 + 1 else p0 + 1 - radiusDivisions

                val p0r = p0 + radiusDivisions

                val p2 = if (p0r < numVerts) p0r else p0r - numVerts
                var p3 = if (p2 < numVerts - 1) p2 + 1 else p2 + 1 - numVerts
                p3 = if (p3 % radiusDivisions != 0) p2 + 1 else p2 + 1 - radiusDivisions

                faces[fIndex + 0] = p2
                faces[fIndex + 1] = p3
                faces[fIndex + 2] = p0
                faces[fIndex + 3] = p2
                faces[fIndex + 4] = p1
                faces[fIndex + 5] = p0
                fIndex += 6

                faces[fIndex + 0] = p2
                faces[fIndex + 1] = p3
                faces[fIndex + 2] = p1
                faces[fIndex + 3] = p0
                faces[fIndex + 4] = p3
                faces[fIndex + 5] = p1
                fIndex += 6
            }
        }

        val mesh = TriangleMesh()
        mesh.points.setAll(*points)
        mesh.texCoords.setAll(*tPoints)
        mesh.faces.setAll(*faces)
        return mesh
    }
}