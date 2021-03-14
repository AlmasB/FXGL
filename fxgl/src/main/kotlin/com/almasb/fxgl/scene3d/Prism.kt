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
 * A 3D prism with a regular n-gon base, where n is number of divisions (must be >= 3).
 * By default, creates a prism with a triangle at its base.
 *
 * Note: adapted from reference [javafx.scene.shape.Cylinder] implementation.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
open class Prism
@JvmOverloads constructor(
        bottomRadius: Double = DEFAULT_RADIUS,
        topRadius: Double = DEFAULT_RADIUS,
        height: Double = DEFAULT_SIZE,
        numDivisions: Int = 3
) : CustomShape3D() {

    private val bottomRadiusProp = newDoubleProperty(bottomRadius)
    private val topRadiusProp = newDoubleProperty(topRadius)
    private val heightProp = newDoubleProperty(height)
    private val numDivisionsProp = newIntProperty(if (numDivisions < 3) 3 else numDivisions)

    var bottomRadius: Double
        get() = bottomRadiusProp.value
        set(value) { bottomRadiusProp.value = value }

    var topRadius: Double
        get() = topRadiusProp.value
        set(value) { topRadiusProp.value = value }

    var height: Double
        get() = heightProp.value
        set(value) { heightProp.value = value }

    var numDivisions: Int
        get() = numDivisionsProp.value
        set(value) { numDivisionsProp.value = value }

    fun bottomRadiusProperty() = bottomRadiusProp
    fun topRadiusProperty() = topRadiusProp
    fun heightProperty() = heightProp
    fun numDivisionsProperty() = numDivisionsProp

    init {
        updateMesh()
    }

    override fun createMesh(): Mesh {
        // adapted from Cylinder::createMesh

        val h = height.toFloat() * 0.5f
        val div = numDivisions
        val topR = topRadius.toFloat()
        val botR = bottomRadius.toFloat()

        // NOTE: still create mesh for degenerated prism
        val nPoints: Int = div * 2 + 2
        val tcCount: Int = (div + 1) * 4 + 1 // 2 cap tex

        val faceCount: Int = div * 4

        val textureDelta = 1f / 256

        val dA: Float = 1f / div

        val points = FloatArray(nPoints * 3)
        val tPoints = FloatArray(tcCount * 2)
        val faces = IntArray(faceCount * 6)
        val smoothing = IntArray(faceCount)

        var pPos = 0
        var tPos = 0

        for (i in 0 until div) {
            val a = dA * i * PI2

            points[pPos + 0] = sinF(a) * botR
            points[pPos + 2] = cosF(a) * botR
            points[pPos + 1] = h

            tPoints[tPos + 0] = 1 - dA * i
            tPoints[tPos + 1] = 1 - textureDelta

            pPos += 3
            tPos += 2
        }

        // top edge
        tPoints[tPos + 0] = 0f
        tPoints[tPos + 1] = 1 - textureDelta
        tPos += 2

        for (i in 0 until div) {
            val a = dA * i * PI2

            points[pPos + 0] = sinF(a) * topR
            points[pPos + 2] = cosF(a) * topR
            points[pPos + 1] = -h

            tPoints[tPos + 0] = 1 - dA * i
            tPoints[tPos + 1] = textureDelta

            pPos += 3
            tPos += 2
        }

        // bottom edge
        tPoints[tPos + 0] = 0f
        tPoints[tPos + 1] = textureDelta
        tPos += 2

        // add cap central points
        points[pPos + 0] = 0f
        points[pPos + 1] = h
        points[pPos + 2] = 0f
        points[pPos + 3] = 0f
        points[pPos + 4] = -h
        points[pPos + 5] = 0f
        pPos += 6

        // add cap central points
        // bottom cap
        for (i in 0..div) {
            val a: Double = if (i < div) dA * i * PI2 else 0.0

            tPoints[tPos + 0] = 0.5f + sinF(a) * 0.5f
            tPoints[tPos + 1] = 0.5f + cosF(a) * 0.5f

            tPos += 2
        }

        // top cap
        for (i in 0..div) {
            val a: Double = if (i < div) dA * i * PI2 else 0.0

            tPoints[tPos + 0] = 0.5f + sinF(a) * 0.5f
            tPoints[tPos + 1] = 0.5f - cosF(a) * 0.5f

            tPos += 2
        }

        tPoints[tPos + 0] = .5f
        tPoints[tPos + 1] = .5f
        tPos += 2

        var fIndex = 0

        // build body faces
        for (p0 in 0 until div) {
            val p1 = p0 + 1
            val p2: Int = p0 + div
            val p3: Int = p1 + div

            // add p0, p1, p2
            faces[fIndex + 0] = p0
            faces[fIndex + 1] = p0
            faces[fIndex + 2] = p2
            faces[fIndex + 3] = p2 + 1
            faces[fIndex + 4] = if (p1 == div) 0 else p1
            faces[fIndex + 5] = p1
            fIndex += 6

            // add p3, p2, p1
            faces[fIndex + 0] = if (p3 % div == 0) p3 - div else p3
            faces[fIndex + 1] = p3 + 1
            faces[fIndex + 2] = if (p1 == div) 0 else p1
            faces[fIndex + 3] = p1
            faces[fIndex + 4] = p2
            faces[fIndex + 5] = p2 + 1
            fIndex += 6
        }

        // build cap faces
        var tStart: Int = (div + 1) * 2
        val t1: Int = (div + 1) * 4
        var p1: Int = div * 2

        // bottom cap
        for (p0 in 0 until div) {
            val p2 = p0 + 1
            val t0 = tStart + p0
            val t2 = t0 + 1

            // add p0, p1, p2
            faces[fIndex + 0] = p0
            faces[fIndex + 1] = t0
            faces[fIndex + 2] = if (p2 == div) 0 else p2
            faces[fIndex + 3] = t2
            faces[fIndex + 4] = p1
            faces[fIndex + 5] = t1
            fIndex += 6
        }

        p1 = div * 2 + 1
        tStart = (div + 1) * 3

        // top cap
        for (p0 in 0 until div) {
            val p2: Int = p0 + 1 + div
            val t0 = tStart + p0
            val t2 = t0 + 1

            faces[fIndex + 0] = p0 + div
            faces[fIndex + 1] = t0
            faces[fIndex + 2] = p1
            faces[fIndex + 3] = t1
            faces[fIndex + 4] = if (p2 % div == 0) p2 - div else p2
            faces[fIndex + 5] = t2
            fIndex += 6
        }

        for (i in 0 until div * 2) {
            smoothing[i] = 1
        }
        for (i in div * 2 until div * 4) {
            smoothing[i] = 2
        }

        val m = TriangleMesh()
        m.points.setAll(*points)
        m.texCoords.setAll(*tPoints)
        m.faces.setAll(*faces)
        m.faceSmoothingGroups.setAll(*smoothing)

        return m
    }
}