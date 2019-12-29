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
 *
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

    fun topLeft(x: Double, y: Double, w: Double, h: Double): NineSliceTextureBuilder {
        return topLeft(Rectangle2D(x, y, w, h))
    }

    fun topLeft(rect: Rectangle2D): NineSliceTextureBuilder {
        topLeft = texture.subTexture(rect)
        return this
    }

    fun top(x: Double, y: Double, w: Double, h: Double): NineSliceTextureBuilder {
        return top(Rectangle2D(x, y, w, h))
    }

    fun top(rect: Rectangle2D): NineSliceTextureBuilder {
        top = texture.subTexture(rect)
        return this
    }

    fun topRight(x: Double, y: Double, w: Double, h: Double): NineSliceTextureBuilder {
        return topRight(Rectangle2D(x, y, w, h))
    }

    fun topRight(rect: Rectangle2D): NineSliceTextureBuilder {
        topRight = texture.subTexture(rect)
        return this
    }

    fun right(x: Double, y: Double, w: Double, h: Double): NineSliceTextureBuilder {
        return right(Rectangle2D(x, y, w, h))
    }

    fun right(rect: Rectangle2D): NineSliceTextureBuilder {
        right = texture.subTexture(rect)
        return this
    }

    fun botRight(x: Double, y: Double, w: Double, h: Double): NineSliceTextureBuilder {
        return botRight(Rectangle2D(x, y, w, h))
    }

    fun botRight(rect: Rectangle2D): NineSliceTextureBuilder {
        botRight = texture.subTexture(rect)
        return this
    }

    fun bot(x: Double, y: Double, w: Double, h: Double): NineSliceTextureBuilder {
        return bot(Rectangle2D(x, y, w, h))
    }

    fun bot(rect: Rectangle2D): NineSliceTextureBuilder {
        bot = texture.subTexture(rect)
        return this
    }

    fun botLeft(x: Double, y: Double, w: Double, h: Double): NineSliceTextureBuilder {
        return botLeft(Rectangle2D(x, y, w, h))
    }

    fun botLeft(rect: Rectangle2D): NineSliceTextureBuilder {
        botLeft = texture.subTexture(rect)
        return this
    }

    fun left(x: Double, y: Double, w: Double, h: Double): NineSliceTextureBuilder {
        return left(Rectangle2D(x, y, w, h))
    }

    fun left(rect: Rectangle2D): NineSliceTextureBuilder {
        left = texture.subTexture(rect)
        return this
    }

    fun center(x: Double, y: Double, w: Double, h: Double): NineSliceTextureBuilder {
        return center(Rectangle2D(x, y, w, h))
    }

    fun center(rect: Rectangle2D): NineSliceTextureBuilder {
        center = texture.subTexture(rect)
        return this
    }

    fun build(width: Int, height: Int): Texture {
        val w = width - left.width - right.width
        val h = height - top.height - bot.height

        check(w > 0 && h > 0) { "Too small" }

        // TODO: cleanup

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