/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.texture

import javafx.scene.effect.BlendMode
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color

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
        BlendMode.SRC_OVER -> TODO()
        BlendMode.SRC_ATOP -> TODO()
        BlendMode.ADD -> ADD_BLEND
        BlendMode.MULTIPLY -> TODO()
        BlendMode.SCREEN -> TODO()
        BlendMode.OVERLAY -> TODO()
        BlendMode.DARKEN -> TODO()
        BlendMode.LIGHTEN -> TODO()
        BlendMode.COLOR_DODGE -> TODO()
        BlendMode.COLOR_BURN -> TODO()
        BlendMode.HARD_LIGHT -> TODO()
        BlendMode.SOFT_LIGHT -> TODO()
        BlendMode.DIFFERENCE -> TODO()
        BlendMode.EXCLUSION -> TODO()
        BlendMode.RED -> RED_BLEND
        BlendMode.GREEN -> GREEN_BLEND
        BlendMode.BLUE -> BLUE_BLEND
    }
}

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

    // TODO: can we parallelize this?
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