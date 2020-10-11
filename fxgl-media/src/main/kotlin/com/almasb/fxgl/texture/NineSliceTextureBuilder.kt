/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.texture

import javafx.geometry.HorizontalDirection
import javafx.geometry.Rectangle2D
import javafx.geometry.VerticalDirection
import javafx.scene.image.Image

/**
 * Used to build textures from 9 partial images: 4 corners, 4 sides, 1 center.
 * The corners remain unchanged where the sides and center widen to fill the desired dimension.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class NineSliceTextureBuilder(val texture: Texture) {

    constructor(image: Image) : this(Texture(image))

    lateinit var topLeft: Texture
    lateinit var top: Texture
    lateinit var topRight: Texture
    lateinit var right: Texture
    lateinit var botRight: Texture
    lateinit var bot: Texture
    lateinit var botLeft: Texture
    lateinit var left: Texture
    lateinit var center: Texture

    fun topLeft(x: Double, y: Double, w: Double, h: Double): NineSliceTextureBuilder = topLeft(Rectangle2D(x, y, w, h))
    fun topLeft(rect: Rectangle2D): NineSliceTextureBuilder = also {
        topLeft = texture.subTexture(rect)
    }

    fun top(x: Double, y: Double, w: Double, h: Double): NineSliceTextureBuilder = top(Rectangle2D(x, y, w, h))
    fun top(rect: Rectangle2D): NineSliceTextureBuilder = also {
        top = texture.subTexture(rect)
    }

    fun topRight(x: Double, y: Double, w: Double, h: Double): NineSliceTextureBuilder = topRight(Rectangle2D(x, y, w, h))
    fun topRight(rect: Rectangle2D): NineSliceTextureBuilder = also {
        topRight = texture.subTexture(rect)
    }

    fun right(x: Double, y: Double, w: Double, h: Double): NineSliceTextureBuilder = right(Rectangle2D(x, y, w, h))
    fun right(rect: Rectangle2D): NineSliceTextureBuilder = also {
        right = texture.subTexture(rect)
    }

    fun botRight(x: Double, y: Double, w: Double, h: Double): NineSliceTextureBuilder = botRight(Rectangle2D(x, y, w, h))
    fun botRight(rect: Rectangle2D): NineSliceTextureBuilder = also {
        botRight = texture.subTexture(rect)
    }

    fun bot(x: Double, y: Double, w: Double, h: Double): NineSliceTextureBuilder = bot(Rectangle2D(x, y, w, h))
    fun bot(rect: Rectangle2D): NineSliceTextureBuilder = also {
        bot = texture.subTexture(rect)
    }

    fun botLeft(x: Double, y: Double, w: Double, h: Double): NineSliceTextureBuilder = botLeft(Rectangle2D(x, y, w, h))
    fun botLeft(rect: Rectangle2D): NineSliceTextureBuilder = also {
        botLeft = texture.subTexture(rect)
    }

    fun left(x: Double, y: Double, w: Double, h: Double): NineSliceTextureBuilder = left(Rectangle2D(x, y, w, h))
    fun left(rect: Rectangle2D): NineSliceTextureBuilder = also {
        left = texture.subTexture(rect)
    }

    fun center(x: Double, y: Double, w: Double, h: Double): NineSliceTextureBuilder = center(Rectangle2D(x, y, w, h))
    fun center(rect: Rectangle2D): NineSliceTextureBuilder = also {
        center = texture.subTexture(rect)
    }

    fun build(width: Int, height: Int): Texture {
        val w = width - left.width - right.width
        val h = height - top.height - bot.height

        check(w > 0 && h > 0) { "The entered dimension are too small to build the texture." }

        val adjustedTop = adjustTextureHorizontally(top, w)

        val row0 = topLeft.superTexture(adjustedTop, HorizontalDirection.RIGHT).superTexture(topRight, HorizontalDirection.RIGHT)

        val adjustedCenterHorizontally: Texture = adjustTextureHorizontally(center, w)

        val adjustedCenterBoth= adjustTextureVertically(adjustedCenterHorizontally, h)

        val newLeft = adjustTextureVertically(left, h)

        val newRight = adjustTextureVertically(right, h)

        val row1 = newLeft.superTexture(adjustedCenterBoth, HorizontalDirection.RIGHT).superTexture(newRight, HorizontalDirection.RIGHT)

        val newBot = adjustTextureHorizontally(bot, w)

        val row2 = botLeft.superTexture(newBot, HorizontalDirection.RIGHT).superTexture(botRight, HorizontalDirection.RIGHT)

        return row0.superTexture(row1, VerticalDirection.DOWN).superTexture(row2, VerticalDirection.DOWN)
    }

    private fun adjustTextureVertically(base: Texture, height: Double): Texture {
        var adjusted = base

        if (base.height > height) {
            adjusted = base.subTexture(Rectangle2D(0.0, 0.0, base.width, height))
        } else if (base.height < height) {
            val times = height.toInt() / base.height.toInt()
            val rem = height.toInt() % base.height.toInt()

            for (i in 1 until times) {
                adjusted = base.superTexture(base, VerticalDirection.DOWN)
            }

            if (rem > 0) {
                adjusted = base.superTexture(base.subTexture(Rectangle2D(0.0, 0.0, base.width, rem.toDouble())), VerticalDirection.DOWN)
            }
        }
        return adjusted
    }

    private fun adjustTextureHorizontally(base: Texture, width: Double): Texture {
        var adjusted = base

        if (base.width > width) {
            adjusted = base.subTexture(Rectangle2D(0.0, 0.0, width, base.height))
        } else if (base.width < width) {
            val times = width.toInt() / base.width.toInt()
            val rem = width.toInt() % base.width.toInt()

            for (i in 1 until times) {
                adjusted = adjusted.superTexture(base, HorizontalDirection.RIGHT)
            }

            if (rem > 0) {
                adjusted = adjusted.superTexture(base.subTexture(Rectangle2D(0.0, 0.0, rem.toDouble(), base.height)), HorizontalDirection.RIGHT)
            }
        }
        return adjusted
    }
}