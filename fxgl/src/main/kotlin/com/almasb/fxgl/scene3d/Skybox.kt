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
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.transform.Rotate

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SkyboxBuilder(val size: Int) {

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

    fun buildImageSkybox(): ImageSkybox {
        return ImageSkybox(
                size,
                front,
                back,
                left,
                right,
                top,
                bot
        )
    }

    fun buildCanvasSkybox(): CanvasSkybox {
        return CanvasSkybox(size)
    }
}

/**
 * A generic skybox whose sides consist of Node objects.
 */
open class NodeSkybox(val size: Int) : Group() {

    val front = Group()
    val back = Group()
    val left = Group()
    val right = Group()
    val top = Group()
    val bot = Group()

    val scale = 32.0

    // TODO: once enabled, translate "t" value should also be dynamically adjusted
//    var scale = 32.0
//        set(value) {
//            field = value
//
//            children.forEach {
//                it.scaleX = value
//                it.scaleY = value
//            }
//        }

    init {
        val sizeX = size * scale
        val t = sizeX / 2.0

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

    private fun toView(node: Node): Node {
        node.scaleX = scale
        node.scaleY = scale

        return node
    }
}

/**
 * A skybox whose sides are Canvas objects, to which users can draw.
 */
class CanvasSkybox(size: Int) : NodeSkybox(size) {

    val frontCanvas = Canvas(size.toDouble(), size.toDouble())
    val backCanvas = Canvas(size.toDouble(), size.toDouble())
    val leftCanvas = Canvas(size.toDouble(), size.toDouble())
    val rightCanvas = Canvas(size.toDouble(), size.toDouble())
    val topCanvas = Canvas(size.toDouble(), size.toDouble())
    val botCanvas = Canvas(size.toDouble(), size.toDouble())

    /**
     * @return all 6 canvas objects of this skybox
     */
    val canvases = listOf(frontCanvas, backCanvas, leftCanvas, rightCanvas, topCanvas, botCanvas)

    init {
        front.children += frontCanvas
        back.children += backCanvas
        left.children += leftCanvas
        right.children += rightCanvas
        top.children += topCanvas
        bot.children += botCanvas
    }
}

class ImageSkybox(
        size: Int,
        front: Image,
        back: Image,
        left: Image,
        right: Image,
        top: Image,
        bot: Image
) : NodeSkybox(size) {

    init {
        this.front.children += toView(front)
        this.back.children += toView(back)
        this.left.children += toView(left)
        this.right.children += toView(right)
        this.top.children += toView(top)
        this.bot.children += toView(bot)
    }

    private fun toView(image: Image): Node {
        return ImageView(image)
    }
}