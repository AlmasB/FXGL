/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.texture

import com.almasb.fxgl.core.View
import javafx.geometry.HorizontalDirection
import javafx.geometry.Rectangle2D
import javafx.geometry.VerticalDirection
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.effect.BlendMode
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import javafx.util.Duration

/**
 * Represents a 2D image which can be set as view for an entity.
 * The size ratio and visible area of the image can be modified as necessary.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 * @apiNote This is essentially a wrapper around [javafx.scene.image.ImageView]
 */
open class Texture : ImageView, View {

    /**
     * Constructs new texture from given image.
     *
     * @param image the JavaFX image data
     */
    constructor(image: Image) : super(image)

    protected constructor() : super()

    val width: Double
        get() = image.width

    val height: Double
        get() = image.height

    /**
     * Converts the texture to animated texture using
     * the whole texture as a single animation channel.
     * Must be in 1 row.
     *
     * @param frames   number of frames in sprite sheet
     * @param duration overall duration (for all frames) of the animation
     * @return new AnimatedTexture
     */
    fun toAnimatedTexture(frames: Int, duration: Duration) = toAnimatedTexture(AnimationChannel(image, duration, frames))

    /**
     * Converts the texture to animated texture.
     *
     * @param defaultChannel the default channel
     * @return new AnimatedTexture
     */
    fun toAnimatedTexture(defaultChannel: AnimationChannel) = AnimatedTexture(defaultChannel)

    /**
     * Call this to create a new texture if you are
     * planning to use the same image as graphics
     * for multiple entities.
     * This is required because same Node can only have 1 parent.
     *
     *
     * Do NOT invoke on instances of StaticAnimatedTexture or
     * AnimatedTexture, use [.toAnimatedTexture]
     * or [.toAnimatedTexture] instead.
     *
     * @return new Texture with same image
     */
    fun copy() = Texture(image)

    /**
     * Given a rectangular area, produces a sub-texture of
     * this texture.
     *
     *
     * Rectangle cannot cover area outside of the original texture
     * image.
     *
     * @param area area of the original texture that represents sub-texture
     * @return sub-texture
     */
    fun subTexture(area: Rectangle2D): Texture {
        val minX = area.minX.toInt()
        val minY = area.minY.toInt()
        val maxX = area.maxX.toInt()
        val maxY = area.maxY.toInt()

        require(minX >= 0) { "minX value of sub-texture cannot be negative" }
        require(minY >= 0) { "minY value of sub-texture cannot be negative" }
        require(maxX <= image.width) { "maxX value of sub-texture cannot be greater than image width" }
        require(maxY <= image.height) { "maxY value of sub-texture cannot be greater than image height" }

        val pixelReader = image.pixelReader
        val image = WritableImage(maxX - minX, maxY - minY)
        val pixelWriter = image.pixelWriter

        for (y in minY until maxY) {
            for (x in minX until maxX) {
                val color = pixelReader.getColor(x, y)
                pixelWriter.setColor(x - minX, y - minY, color)
            }
        }

        return Texture(image)
    }

    /**
     * Generates a new texture which combines this and given texture.
     * The given texture is appended based on the direction provided.
     *
     * @param other the texture to append to this one
     * @param direction the direction to append from
     * @return new combined texture
     */
    fun superTexture(other: Texture, direction: HorizontalDirection): Texture {
        val leftImage: Image
        val rightImage: Image

        if (direction == HorizontalDirection.LEFT) {
            leftImage = other.image
            rightImage = this.image
        } else {
            leftImage = this.image
            rightImage = other.image
        }

        val width = (leftImage.width + rightImage.width).toInt()
        val height = Math.max(leftImage.height, rightImage.height).toInt()

        val leftReader = leftImage.pixelReader
        val rightReader = rightImage.pixelReader
        val image = WritableImage(width, height)
        val pixelWriter = image.pixelWriter

        for (y in 0 until height) {
            for (x in 0 until width) {
                val color: Color
                if (x < leftImage.width) {
                    if (y < leftImage.height) {
                        color = leftReader.getColor(x, y)
                    } else {
                        color = Color.TRANSPARENT
                    }
                } else {
                    if (y < rightImage.height) {
                        color = rightReader.getColor(x - leftImage.width.toInt(), y)
                    } else {
                        color = Color.TRANSPARENT
                    }
                }

                pixelWriter.setColor(x, y, color)
            }
        }

        return Texture(image)
    }

    /**
     * Generates a new texture which combines this and given texture.
     * The given texture is appended based on the direction provided.
     *
     * @param other the texture to append to this one
     * @param direction the direction to append from
     * @return new combined texture
     */
    fun superTexture(other: Texture, direction: VerticalDirection): Texture {
        val topImage: Image
        val bottomImage: Image

        if (direction == VerticalDirection.DOWN) {
            topImage = this.image
            bottomImage = other.image
        } else {
            topImage = other.image
            bottomImage = this.image
        }

        val width = Math.max(topImage.width, bottomImage.width).toInt()
        val height = (topImage.height + bottomImage.height).toInt()

        val topReader = topImage.pixelReader
        val bottomReader = bottomImage.pixelReader
        val image = WritableImage(width, height)
        val pixelWriter = image.pixelWriter

        for (y in 0 until height) {
            for (x in 0 until width) {
                val color: Color
                if (y < topImage.height) {
                    if (x < topImage.width) {
                        color = topReader.getColor(x, y)
                    } else {
                        color = Color.TRANSPARENT
                    }
                } else {
                    if (x < bottomImage.width) {
                        color = bottomReader.getColor(x, y - topImage.height.toInt())
                    } else {
                        color = Color.TRANSPARENT
                    }
                }

                pixelWriter.setColor(x, y, color)
            }
        }

        return Texture(image)
    }

    /**
     * @return grayscale version of the texture
     */
    fun toGrayscale() = Texture(image.map { it.copy(it.color.grayscale()) })

    fun invert() = Texture(image.map { it.copy(it.color.invert()) })

    fun brighter() = Texture(image.map { it.copy(it.color.brighter()) })

    fun darker() = Texture(image.map { it.copy(it.color.darker()) })

    fun saturate() = Texture(image.map { it.copy(it.color.saturate()) })

    fun desaturate() = Texture(image.map { it.copy(it.color.desaturate()) })

    /**
     * Discoloring is done via setting each pixel to white but
     * preserving opacity (alpha channel).
     *
     * @return texture with image discolored
     */
    fun discolor() = Texture(image.map { it.copy(Color.color(1.0, 1.0, 1.0, it.A)) })

    /**
     * Multiplies this texture's pixel color with given color.
     *
     * @param color to use
     * @return new colorized texture
     */
    fun multiplyColor(color: Color): Texture {
        return Texture(image.map { it.copy(Color.color(
                it.R * color.red,
                it.G * color.green,
                it.B * color.blue,
                it.A * color.opacity
        )) })
    }

    /**
     * Colorizes this texture's pixels with given color.
     *
     * @param color to use
     * @return new colorized texture
     */
    fun toColor(color: Color): Texture {
        val discolored = discolor()
        val colored = discolored.multiplyColor(color)
        discolored.dispose()

        return colored
    }

    /**
     * Replaces all [oldColor] pixels with [newColor] pixels.
     */
    fun replaceColor(oldColor: Color, newColor: Color): Texture {
        val newImage = image.map {
            val c = if (it.color == oldColor) {
                newColor
            } else {
                it.color
            }

            it.copy(c)
        }

        return Texture(newImage)
    }

    /**
     * @param color transparent color
     * @return new texture whose pixels with given color are set to transparent
     */
    fun transparentColor(color: Color) = replaceColor(color, Color.TRANSPARENT)

    fun blend(node: Node, blendMode: BlendMode) = blend(toImage(node), blendMode)

    /**
     * @param backgroundImage the image with which to blend
     * @param blendMode blend mode
     * @return new texture using a blended image of this texture
     */
    fun blend(backgroundImage: Image, blendMode: BlendMode) = Texture(backgroundImage.map(image, blendMode.operation()))

    @JvmOverloads fun outline(color: Color, offset: Int = 1): Texture {
        val view = Group()

        // using nodes rather than pixel reader / writer is less tricky and possibly faster
        // because when writing pixels we have to go through each pixel to check its transparency
        // so we don't overwrite the underlying pixel
        val coloredTexture = toColor(color)

        val outline1 = coloredTexture
        outline1.translateX = offset.toDouble()

        val outline2 = coloredTexture.copy()
        outline2.translateX = -offset.toDouble()

        val outline3 = coloredTexture.copy()
        outline3.translateY = offset.toDouble()

        val outline4 = coloredTexture.copy()
        outline4.translateY = -offset.toDouble()

        view.children.addAll(outline1, outline2, outline3, outline4, copy())

        return Texture(toImage(view))
    }

    /**
     * Set texture data by copying it from other texture.
     *
     * @param other the texture to copy from
     */
    fun set(other: Texture) {
        fitWidth = other.fitWidth
        fitHeight = other.fitHeight
        image = other.image
    }

    override fun onUpdate(tpf: Double) {
    }

    override fun getNode(): Node {
        return this
    }

    override fun dispose() {
        image = null
    }
}
