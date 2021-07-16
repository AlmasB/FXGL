/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene3d.obj

import javafx.scene.paint.Color
import javafx.scene.paint.Material
import javafx.scene.paint.PhongMaterial
import java.net.URL

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class ObjData(val url: URL) {
    val groups = arrayListOf<ObjGroup>()
    val vertices = arrayListOf<Float>()
    val vertexNormals = arrayListOf<Float>()
    val vertexTextures = arrayListOf<Float>()

    val materials = hashMapOf<String, Material>()
    val ambientColors = hashMapOf<Material, Color>()

    val currentGroup: ObjGroup
        get() {
            // it is possible there are no groups in the obj file,
            // in which case when asked for current group return default
            // TODO: extract string
            if (groups.isEmpty())
                groups += ObjGroup("default")

            return groups.last()
        }
}

internal class ObjGroup(val name: String) {
    val subGroups = arrayListOf<SubGroup>(SubGroup())

    val currentSubGroup
        get() = subGroups.last()
}

internal class SubGroup {
    val faces = arrayListOf<Int>()
    val smoothingGroups = arrayListOf<Int>()

    // as per OBJ file spec, default is white
    var material: Material = PhongMaterial(Color.WHITE)
    var ambientColor: Color? = null

    var smoothingGroup = -1
}

internal class MtlData(
        /**
         * URL of the .mtl file.
         */
        val url: URL) {

    val materials = hashMapOf<String, Material>()
    val ambientColors = hashMapOf<Material, Color>()

    lateinit var currentMaterial: PhongMaterial
}