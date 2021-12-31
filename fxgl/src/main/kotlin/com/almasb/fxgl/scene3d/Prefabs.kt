/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene3d

import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.dsl.animationBuilder
import com.almasb.fxgl.dsl.components.ActivatorComponent
import com.almasb.fxgl.dsl.entityBuilder
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.component.Component
import javafx.geometry.Point3D
import javafx.scene.shape.Sphere
import javafx.util.Duration

/**
 * TODO: EXPERIMENTAL API, return models or entities, correct name? API?
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Prefabs {

    companion object {
        @JvmStatic fun newDoor(): Entity {
            return entityBuilder()
                    .with(ActivatorComponent())
                    .with(DoorComponent())
                    .build()
        }
    }
}

class DoorComponent : Component() {

    private lateinit var activator: ActivatorComponent

    val frame = Model3D()
    val panel = Model3D()
    val handle = Sphere(0.03)

    var openAnimation: Animation<*>

    init {
        val left = Cuboid(0.1, 2.0, 0.1)
        val right = Cuboid(0.1, 2.0, 0.1)
        val top = Cuboid(1.0, 0.1, 0.1)

        left.translateX = -0.5 + 0.05
        right.translateX = 0.5 - 0.05
        top.translateY = -1.0

        frame.children.addAll(left, right, top)

        val main = Cuboid(0.8, 1.93, 0.05)

        handle.translateX = main.width * 0.45
        handle.translateZ = -handle.radius

        panel.translateY = 0.02

        panel.children.addAll(main, handle)

        openAnimation = animationBuilder()
                .duration(Duration.seconds(2.5))
                .rotate(panel)
                .origin(Point3D(-main.width / 2, 0.0, 0.0))
                .from(Point3D(0.0, 0.0, 0.0))
                .to(Point3D(0.0, 90.0, 0.0))
                .build()
    }

    override fun onAdded() {
        entity.viewComponent.addChild(frame)
        entity.viewComponent.addChild(panel)

        activator.valueProperty().addListener { _, wasOpen, isOpen ->
            if (isOpen) {
                openAnimation.stop()
                openAnimation.start()
            } else {
                openAnimation.stop()
                openAnimation.startReverse()
            }
        }
    }

    override fun onUpdate(tpf: Double) {
        openAnimation.onUpdate(tpf)
    }
}