/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene3d

import com.almasb.fxgl.core.Copyable
import javafx.scene.Group
import javafx.scene.paint.Color
import javafx.scene.paint.Material
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.MeshView
import javafx.scene.shape.TriangleMesh
import java.util.stream.Collectors
import java.util.stream.IntStream

/**
 * TODO: clean up API and add doc
 * TODO: scale(Point3D), which modifies the mesh, not scaleXYZ
 * TODO: allow setting which way is up, e.g. Z-up
 *
 * A container for one or more [javafx.scene.shape.MeshView].
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
open class Model3D : Group(), Copyable<Model3D> {

    var material: Material = PhongMaterial(Color.WHITE)
        set(value) {
            field = value

            models.forEach { it.material = value }
            meshViews.forEach { it.material = value }
        }

    val models = arrayListOf<Model3D>()
    val meshViews = arrayListOf<MeshView>()

    val vertices: List<CustomShape3D.MeshVertex> by lazy {
        val list = meshViews.map { it.mesh }.flatMap {
            toVertices(it as TriangleMesh)
        }

        list + models.flatMap { it.vertices }
    }

    private fun toVertices(mesh: TriangleMesh): List<CustomShape3D.MeshVertex> {
        val triMesh = mesh

        val numVertices = triMesh.points.size() / 3

        return IntStream.range(0, numVertices)
                .mapToObj { CustomShape3D.MeshVertex(triMesh.points, it * 3) }
                .collect(Collectors.toList())
    }

    fun addMeshView(view: MeshView) {
        meshViews += view
        children += view

        //view.material = material
    }

    fun removeMeshView(view: MeshView) {
        meshViews -= view
        children -= view
    }

    fun addModel(model: Model3D) {
        models += model
        children += model

        //model.material = material
    }

    fun removeModel(model: Model3D) {
        models -= model
        children -= model
    }

    override fun copy(): Model3D {
        val copy = Model3D()

        // TODO: handle materials?
        models.forEach {
            copy.addModel(it.copy())
        }

        meshViews.forEach { original ->
            copy.addMeshView(MeshView(original.mesh).also { it.material = original.material })
        }

        return copy
    }
}