/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.texture

import com.almasb.fxgl.core.concurrent.Async
import javafx.geometry.HorizontalDirection
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.SnapshotParameters
import javafx.scene.effect.BlendMode
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import java.util.concurrent.Callable
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min



private val image: Image by lazy {
    val group = Group()
    val size = 32.0

    val r1 = Rectangle(size, size, Color.BLACK)
    val r2 = Rectangle(size, size, Color.HOTPINK)
    val r3 = Rectangle(size, size, Color.PURPLE)
    val r4 = Rectangle(size, size, Color.BLACK)

    r2.translateX = size
    r3.translateY = size
    r4.translateX = size
    r4.translateY = size

    r1.stroke = Color.GRAY
    r2.stroke = Color.GRAY
    r3.stroke = Color.GRAY
    r4.stroke = Color.GRAY

    group.children.addAll(r1, r2, r3, r4)

    var result: Image? = null

    Async.startAsyncFX {
        result = group.snapshot(null, null)
    }.await()

    result!!
}

fun getDummyImage() = image

fun toImage(node: Node): Image = Async.startAsyncFX(Callable {
    val params = SnapshotParameters()
    params.fill = Color.TRANSPARENT
    node.snapshot(params, null)
}).await()

fun merge(images: List<Image>): Image {
    if (images.isEmpty())
        return getDummyImage()

    if (images.size == 1)
        return images.first()

    var texture = Texture(images.first())

    images.drop(1).forEach {
        texture = texture.superTexture(Texture(it), HorizontalDirection.RIGHT)
    }

    return texture.image
}

/**
 * Resize image, with preserveRatio set to false by default.
 * If preserveRatio is true, vertical scaling will be dependent on horizontal scaling and original image proportion will be reserved.
 */
fun resize(image : Image, targetWidth: Int, targetHeight: Int): Image {
    return resize(image, targetWidth, targetHeight, false)
}

/**
 * Resize image.
 * If preserveRatio is true, vertical scaling will be dependent on horizontal scaling and original image proportion will be reserved.
 */
fun resize(image : Image, targetWidth: Int, targetHeight: Int, preserveRatio: Boolean): Image {
    val width = image.width.toInt()
    val height = image.height.toInt()
    val scaleHorizontal = width.toDouble() / targetWidth
    var scaleVertical = height.toDouble() / targetHeight
    if (preserveRatio)
        scaleVertical = scaleHorizontal

    val output = WritableImage(targetWidth, targetHeight)

    val reader = image.pixelReader
    val writer = output.pixelWriter

    for (y in 0 until targetHeight)
        for (x in 0 until targetWidth)
        {
            val argb = reader.getArgb((x * scaleHorizontal).toInt(), (y * scaleVertical).toInt())
            writer.setArgb(x, y, argb)
        }

    return output
}

data class Pixel(val x: Int, val y: Int, val color: Color, val parent: Image) {

    val A = color.opacity
    val R = color.red
    val G = color.green
    val B = color.blue

    fun copy(newColor: Color): Pixel {
        return Pixel(x, y, newColor, parent)
    }
}

fun BlendMode.operation(): (Pixel, Pixel) -> Pixel {
    return when (this) {
        BlendMode.SRC_OVER -> SRC_OVER_BLEND
        BlendMode.SRC_ATOP -> SRC_ATOP_BLEND
        BlendMode.ADD -> ADD_BLEND
        BlendMode.MULTIPLY -> MULTIPLY_BLEND
        BlendMode.SCREEN -> SCREEN_BLEND
        BlendMode.OVERLAY -> OVERLAY_BLEND
        BlendMode.DARKEN -> DARKEN_BLEND
        BlendMode.LIGHTEN -> LIGHTEN_BLEND
        BlendMode.COLOR_DODGE -> COLOR_DODGE_BLEND
        BlendMode.COLOR_BURN -> COLOR_BURN_BLEND
        BlendMode.HARD_LIGHT -> HARD_LIGHT_BLEND
        BlendMode.SOFT_LIGHT -> SOFT_LIGHT_BLEND
        BlendMode.DIFFERENCE -> DIFFERENCE_BLEND
        BlendMode.EXCLUSION -> EXCLUSION_BLEND
        BlendMode.RED -> RED_BLEND
        BlendMode.GREEN -> GREEN_BLEND
        BlendMode.BLUE -> BLUE_BLEND
    }
}

internal val SRC_OVER_BLEND: (Pixel, Pixel) -> Pixel = { bot, top ->
    if (top.color == Color.TRANSPARENT) {
        bot.copy(Color.TRANSPARENT)
    } else {
        val color = Color.color(
                top.R + bot.R * (1 - top.R),
                top.G + bot.G * (1 - top.G),
                top.B + bot.B * (1 - top.B),
                top.A + bot.A * (1 - top.A)
        )

        bot.copy(color)
    }
}

internal val SRC_ATOP_BLEND: (Pixel, Pixel) -> Pixel = { bot, top ->
    if (top.color == Color.TRANSPARENT) {
        bot.copy(Color.TRANSPARENT)
    } else {
        val color = Color.color(
                top.R * bot.A + bot.R * (1 - top.R),
                top.G * bot.A + bot.G * (1 - top.G),
                top.B * bot.A + bot.B * (1 - top.B),
                bot.A
        )

        bot.copy(color)
    }
}

/*
 * In blending functions below, bot is the existing color (dst)
 * and top is the new color (src).
 * In terms of textures it's top (src) drawn over bot (dst).
 */

internal val ADD_BLEND: (Pixel, Pixel) -> Pixel = { p1, p2 ->
    if (p2.color == Color.TRANSPARENT) {
        p1.copy(Color.TRANSPARENT)
    } else {
        val color = Color.color(
                minOf(1.0, p1.color.red + p2.color.red),
                minOf(1.0, p1.color.green + p2.color.green),
                minOf(1.0, p1.color.blue + p2.color.blue),
                minOf(1.0, p1.color.opacity + p2.color.opacity)
        )

        p1.copy(color)
    }
}

internal val MULTIPLY_BLEND: (Pixel, Pixel) -> Pixel = { bot, top ->
    if (top.color == Color.TRANSPARENT) {
        bot.copy(Color.TRANSPARENT)
    } else {
        val color = Color.color(
                top.R * bot.R,
                top.G * bot.G,
                top.B * bot.B,
                top.A + bot.A * (1 - top.A)
        )

        bot.copy(color)
    }
}

internal val SCREEN_BLEND: (Pixel, Pixel) -> Pixel = { bot, top ->
    if (top.color == Color.TRANSPARENT) {
        bot.copy(Color.TRANSPARENT)
    } else {
        val color = Color.color(
                1 - (1 - top.R) * (1 - bot.R),
                1 - (1 - top.G) * (1 - bot.G),
                1 - (1 - top.B) * (1 - bot.B),
                top.A + bot.A * (1 - top.A)
        )

        bot.copy(color)
    }
}

/**
 * See https://en.wikipedia.org/wiki/Blend_modes#Overlay
 */
internal val OVERLAY_BLEND: (Pixel, Pixel) -> Pixel = { bot, top ->
    if (top.color == Color.TRANSPARENT) {
        bot.copy(Color.TRANSPARENT)
    } else {

        val r = if (bot.R < 0.5) {
            2 * bot.R * top.R
        } else {
            1 - 2 * (1 - bot.R) * (1 - top.R)
        }

        val g = if (bot.G < 0.5) {
            2 * bot.G * top.G
        } else {
            1 - 2 * (1 - bot.G) * (1 - top.G)
        }

        val b = if (bot.B < 0.5) {
            2 * bot.B * top.B
        } else {
            1 - 2 * (1 - bot.B) * (1 - top.B)
        }

        val color = Color.color(
                r,
                g,
                b,
                top.A + bot.A * (1 - top.A)
        )

        bot.copy(color)
    }
}

internal val DARKEN_BLEND: (Pixel, Pixel) -> Pixel = { bot, top ->
    if (top.color == Color.TRANSPARENT) {
        bot.copy(Color.TRANSPARENT)
    } else {
        val color = Color.color(
                min(top.R, bot.R),
                min(top.G, bot.G),
                min(top.B, bot.B),
                top.A + bot.A * (1 - top.A)
        )

        bot.copy(color)
    }
}

internal val LIGHTEN_BLEND: (Pixel, Pixel) -> Pixel = { bot, top ->
    if (top.color == Color.TRANSPARENT) {
        bot.copy(Color.TRANSPARENT)
    } else {
        val color = Color.color(
                max(top.R, bot.R),
                max(top.G, bot.G),
                max(top.B, bot.B),
                top.A + bot.A * (1 - top.A)
        )

        bot.copy(color)
    }
}

internal val COLOR_DODGE_BLEND: (Pixel, Pixel) -> Pixel = { bot, top ->
    if (top.color == Color.TRANSPARENT) {
        bot.copy(Color.TRANSPARENT)
    } else {
        val color = Color.color(
                bot.R / (1 - top.R),
                bot.G / (1 - top.G),
                bot.B / (1 - top.B),
                top.A + bot.A * (1 - top.A)
        )

        bot.copy(color)
    }
}

internal val COLOR_BURN_BLEND: (Pixel, Pixel) -> Pixel = { bot, top ->
    if (top.color == Color.TRANSPARENT) {
        bot.copy(Color.TRANSPARENT)
    } else {
        val color = Color.color(
                1 - ((1 - bot.R) / top.R),
                1 - ((1 - bot.G) / top.G),
                1 - ((1 - bot.B) / top.B),
                top.A + bot.A * (1 - top.A)
        )

        bot.copy(color)
    }
}

/**
 * Like OVERLAY but top and bot are swapped.
 */
internal val HARD_LIGHT_BLEND: (Pixel, Pixel) -> Pixel = { bot, top ->
    if (top.color == Color.TRANSPARENT) {
        bot.copy(Color.TRANSPARENT)
    } else {

        val r = if (top.R < 0.5) {
            2 * top.R * bot.R
        } else {
            1 - 2 * (1 - top.R) * (1 - bot.R)
        }

        val g = if (top.G < 0.5) {
            2 * top.G * bot.G
        } else {
            1 - 2 * (1 - top.G) * (1 - bot.G)
        }

        val b = if (top.B < 0.5) {
            2 * top.B * bot.B
        } else {
            1 - 2 * (1 - top.B) * (1 - bot.B)
        }

        val color = Color.color(
                r,
                g,
                b,
                top.A + bot.A * (1 - top.A)
        )

        bot.copy(color)
    }
}

/**
 * See https://en.wikipedia.org/wiki/Blend_modes#Soft_Light
 */
internal val SOFT_LIGHT_BLEND: (Pixel, Pixel) -> Pixel = { bot, top ->
    if (top.color == Color.TRANSPARENT) {
        bot.copy(Color.TRANSPARENT)
    } else {

        val r = (1 - 2 * top.R) * bot.R * bot.R + 2 * top.R * bot.R

        val g = (1 - 2 * top.G) * bot.G * bot.G + 2 * top.G * bot.G

        val b = (1 - 2 * top.B) * bot.B * bot.B + 2 * top.B * bot.B

        val color = Color.color(
                r,
                g,
                b,
                top.A + bot.A * (1 - top.A)
        )

        bot.copy(color)
    }
}

internal val DIFFERENCE_BLEND: (Pixel, Pixel) -> Pixel = { bot, top ->
    if (top.color == Color.TRANSPARENT) {
        bot.copy(Color.TRANSPARENT)
    } else {

        val color = Color.color(
                abs(top.R - bot.R),
                abs(top.G - bot.G),
                abs(top.B - bot.B),
                top.A + bot.A * (1 - top.A)
        )

        bot.copy(color)
    }
}

internal val EXCLUSION_BLEND: (Pixel, Pixel) -> Pixel = { bot, top ->
    if (top.color == Color.TRANSPARENT) {
        bot.copy(Color.TRANSPARENT)
    } else {

        val color = Color.color(
                top.R + bot.R - 2 * top.R * bot.R,
                top.G + bot.G - 2 * top.G * bot.G,
                top.B + bot.B - 2 * top.B * bot.B,
                top.A + bot.A * (1 - top.A)
        )

        bot.copy(color)
    }
}

internal val RED_BLEND: (Pixel, Pixel) -> Pixel = { bot, top ->
    if (top.color == Color.TRANSPARENT) {
        bot.copy(Color.TRANSPARENT)
    } else {
        val color = Color.color(
                top.R,
                bot.G,
                bot.B,
                top.A + bot.A * (1 - top.A)
        )

        bot.copy(color)
    }
}

internal val GREEN_BLEND: (Pixel, Pixel) -> Pixel = { bot, top ->
    if (top.color == Color.TRANSPARENT) {
        bot.copy(Color.TRANSPARENT)
    } else {
        val color = Color.color(
                bot.R,
                top.G,
                bot.B,
                top.A + bot.A * (1 - top.A)
        )

        bot.copy(color)
    }
}

internal val BLUE_BLEND: (Pixel, Pixel) -> Pixel = { bot, top ->
    if (top.color == Color.TRANSPARENT) {
        bot.copy(Color.TRANSPARENT)
    } else {
        val color = Color.color(
                bot.R,
                bot.G,
                top.B,
                top.A + bot.A * (1 - top.A)
        )

        bot.copy(color)
    }
}

fun Image.map(f: (Pixel) -> Pixel): Image {

    val w = this.width.toInt()
    val h = this.height.toInt()

    val reader = this.pixelReader
    val newImage = WritableImage(w, h)
    val writer = newImage.pixelWriter

    for (y in 0 until h) {
        for (x in 0 until w) {

            val pixel = Pixel(x, y, reader.getColor(x, y), this)
            val newPixel = f.invoke(pixel)

            writer.setColor(x, y, newPixel.color)
        }
    }

    return newImage
}

fun Image.map(overlay: Image, f: (Pixel, Pixel) -> Pixel): Image {

    val w = this.width.toInt()
    val h = this.height.toInt()

    val reader = this.pixelReader
    val overlayReader = overlay.pixelReader
    val newImage = WritableImage(w, h)
    val writer = newImage.pixelWriter

    for (y in 0 until h) {
        for (x in 0 until w) {

            val pixel1 = Pixel(x, y, reader.getColor(x, y), this)
            val pixel2 = Pixel(x, y, overlayReader.getColor(x, y), overlay)
            val newPixel = f.invoke(pixel1, pixel2)

            writer.setColor(x, y, newPixel.color)
        }
    }

    return newImage
}