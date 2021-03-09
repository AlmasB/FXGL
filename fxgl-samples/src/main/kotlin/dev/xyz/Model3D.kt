/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev.xyz

import javafx.scene.Group
import javafx.scene.shape.MeshView

/**
 * A container for one or more [javafx.scene.shape.MeshView].
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
open class Model3D : Group() {

    val meshViews = arrayListOf<MeshView>()

    fun addMeshView(view: MeshView) {
        meshViews += view
        children += view
    }

    fun removeMeshView(view: MeshView) {
        meshViews -= view
        children -= view
    }
}