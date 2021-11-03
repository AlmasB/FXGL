/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene3d

import com.almasb.fxgl.dsl.image
import com.almasb.fxgl.texture.getDummyImage
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.transform.Rotate

/**
 * TODO: CanvasSkybox, NodeSkybox
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SkyboxBuilder(val width: Int,
                    val height: Int) {

    private var front: Image = getDummyImage()
    private var back: Image = getDummyImage()
    private var left: Image = getDummyImage()
    private var right: Image = getDummyImage()
    private var top: Image = getDummyImage()
    private var bot: Image = getDummyImage()

    fun front(imageName: String) = this.also {
        front = image(imageName)
    }

    fun back(imageName: String) = this.also {
        back = image(imageName)
    }

    fun left(imageName: String) = this.also {
        left = image(imageName)
    }

    fun right(imageName: String) = this.also {
        right = image(imageName)
    }

    fun top(imageName: String) = this.also {
        top = image(imageName)
    }

    fun bot(imageName: String) = this.also {
        bot = image(imageName)
    }

    fun front(image: Image) = this.also {
        front = image
    }

    fun back(image: Image) = this.also {
        back = image
    }

    fun left(image: Image) = this.also {
        left = image
    }

    fun right(image: Image) = this.also {
        right = image
    }

    fun top(image: Image) = this.also {
        top = image
    }

    fun bot(image: Image) = this.also {
        bot = image
    }

    fun build(): Skybox {
        return Skybox(
                width,
                height,
                front,
                back,
                left,
                right,
                top,
                bot
        )
    }
}

class Skybox(
        val width: Int,
        val height: Int,
        val front: Image,
        val back: Image,
        val left: Image,
        val right: Image,
        val top: Image,
        val bot: Image
) : Group() {

    init {
        // TODO: use both width and height
        // TODO: why 32?
        val size = width * 32
        val t = size / 2.0

        children.addAll(
                toView(front).also {
                    it.translateZ = t
                },
                toView(back).also {
                    it.translateZ = -t
                    it.rotationAxis = Rotate.Y_AXIS
                    it.rotate = 180.0
                },
                toView(left).also {
                    it.translateX = -t
                    it.rotationAxis = Rotate.Y_AXIS
                    it.rotate = -90.0
                },
                toView(right).also {
                    it.translateX = t
                    it.rotationAxis = Rotate.Y_AXIS
                    it.rotate = 90.0
                },
                toView(top).also {
                    it.translateY = -t
                    it.rotationAxis = Rotate.X_AXIS
                    it.rotate = 90.0
                },
                toView(bot).also {
                    it.translateY = t
                    it.rotationAxis = Rotate.X_AXIS
                    it.rotate = -90.0
                }
        )
    }

    private fun toView(image: Image): Node {
        val view = ImageView(image)

        // TODO: scale differently?
        view.isSmooth = true
        view.scaleX = 32.0
        view.scaleY = 32.0
        return view
    }
}