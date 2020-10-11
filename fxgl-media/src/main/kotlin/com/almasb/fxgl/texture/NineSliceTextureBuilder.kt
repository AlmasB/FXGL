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

        var newTop = top

        if (top.width > w) {
            newTop = top.subTexture(Rectangle2D(0.0, 0.0, w, top.height))
        } else if (top.width < w) {
            val times = w.toInt() / top.width.toInt()
            val rem = w.toInt() % top.width.toInt()

            for (i in 1 until times) {
                newTop = newTop.superTexture(top, HorizontalDirection.RIGHT)
            }

            if (rem > 0) {
                newTop = newTop.superTexture(top.subTexture(Rectangle2D(0.0, 0.0, rem.toDouble(), top.height)), HorizontalDirection.RIGHT)
            }
        }

        val row0 = topLeft.superTexture(newTop, HorizontalDirection.RIGHT).superTexture(topRight, HorizontalDirection.RIGHT)

        var newCenter: Texture = center

        if (newCenter.width > w) {
            newCenter = newCenter.subTexture(Rectangle2D(0.0, 0.0, w, newCenter.height))
        } else if (newCenter.width < w) {
            val times = w.toInt() / newCenter.width.toInt()
            val rem = w.toInt() % newCenter.width.toInt()

            for (i in 1 until times) {
                newCenter = newCenter.superTexture(center, HorizontalDirection.RIGHT)
            }

            if (rem > 0) {
                newCenter = newCenter.superTexture(center.subTexture(Rectangle2D(0.0, 0.0, rem.toDouble(), center.height)), HorizontalDirection.RIGHT)
            }
        }

        val center2 = newCenter

        if (newCenter.height > h) {
            newCenter = newCenter.subTexture(Rectangle2D(0.0, 0.0, newCenter.width, h))
        } else if (newCenter.height < h) {
            val times = h.toInt() / newCenter.height.toInt()
            val rem = h.toInt() % newCenter.height.toInt()

            for (i in 1 until times) {
                newCenter = newCenter.superTexture(center2, VerticalDirection.DOWN)
            }

            if (rem > 0) {
                newCenter = newCenter.superTexture(center2.subTexture(Rectangle2D(0.0, 0.0, center2.width, rem.toDouble())), VerticalDirection.DOWN)
            }
        }



        var newLeft = left
        if (newLeft.height > h) {
            newLeft = left.subTexture(Rectangle2D(0.0, 0.0, left.width, h))
        } else if (newLeft.height < h) {
            val times = h.toInt() / left.height.toInt()
            val rem = h.toInt() % left.height.toInt()

            for (i in 1 until times) {
                newLeft = newLeft.superTexture(left, VerticalDirection.DOWN)
            }

            if (rem > 0) {
                newLeft = newLeft.superTexture(left.subTexture(Rectangle2D(0.0, 0.0, left.width, rem.toDouble())), VerticalDirection.DOWN)
            }
        }

        var newRight = right
        if (newRight.height > h) {
            newRight = right.subTexture(Rectangle2D(0.0, 0.0, right.width, h))
        } else if (newRight.height < h) {
            val times = h.toInt() / right.height.toInt()
            val rem = h.toInt() % right.height.toInt()

            for (i in 1 until times) {
                newRight = newRight.superTexture(right, VerticalDirection.DOWN)
            }

            if (rem > 0) {
                newRight = newRight.superTexture(right.subTexture(Rectangle2D(0.0, 0.0, right.width, rem.toDouble())), VerticalDirection.DOWN)
            }
        }

        val row1 = newLeft.superTexture(newCenter, HorizontalDirection.RIGHT).superTexture(newRight, HorizontalDirection.RIGHT)



        var newBot = bot
        if (newBot.width > w) {
            newBot = bot.subTexture(Rectangle2D(0.0, 0.0, w, bot.height))
        } else if (newBot.width < w) {
            val times = w.toInt() / top.width.toInt()
            val rem = w.toInt() % top.width.toInt()

            for (i in 1 until times) {
                newBot = newBot.superTexture(bot, HorizontalDirection.RIGHT)
            }

            if (rem > 0) {
                newBot = newBot.superTexture(bot.subTexture(Rectangle2D(0.0, 0.0, rem.toDouble(), bot.height)), HorizontalDirection.RIGHT)
            }
        }

        val row2 = botLeft.superTexture(newBot, HorizontalDirection.RIGHT).superTexture(botRight, HorizontalDirection.RIGHT)

        return row0.superTexture(row1, VerticalDirection.DOWN).superTexture(row2, VerticalDirection.DOWN)
    }
}