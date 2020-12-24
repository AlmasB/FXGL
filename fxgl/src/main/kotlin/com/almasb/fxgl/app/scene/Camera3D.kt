/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.scene

import com.almasb.fxgl.entity.components.TransformComponent
import javafx.scene.PerspectiveCamera
import javafx.scene.transform.Rotate
import javafx.scene.transform.Translate

/**
 * A wrapper around JavaFX [PerspectiveCamera] that provides convenience functions.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Camera3D {

    private val translate = Translate(0.0, 0.0, 0.0)
    private val rotateX = Rotate(0.0, Rotate.X_AXIS)
    private val rotateY = Rotate(0.0, Rotate.Y_AXIS)
    private val rotateZ = Rotate(0.0, Rotate.Z_AXIS)

    val transform = TransformComponent()

    val perspectiveCamera = PerspectiveCamera(true)

    init {
        translate.xProperty().bind(transform.xProperty())
        translate.yProperty().bind(transform.yProperty())
        translate.zProperty().bind(transform.zProperty())

        rotateX.angleProperty().bind(transform.rotationXProperty())
        rotateY.angleProperty().bind(transform.rotationYProperty())
        rotateZ.angleProperty().bind(transform.rotationZProperty())

        transform.translateZ(-15.0)

        perspectiveCamera.transforms.addAll(translate, rotateY, rotateX)

        perspectiveCamera.fieldOfView = 60.0
        perspectiveCamera.farClip = 1000.0
    }
}