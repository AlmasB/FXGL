/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.texture

import com.almasb.fxgl.animation.AnimatedImage
import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.logging.Logger
import javafx.geometry.Rectangle2D
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.SnapshotParameters
import javafx.scene.effect.BlendMode
import javafx.scene.image.*
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.Base64
import java.util.concurrent.Callable
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * A collection of functions to be used with [Image].
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

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

fun toPixels(image: Image): List<Pixel> {
    val result = arrayListOf<Pixel>()
    image.map {
        result += it
        it
    }
    return result
}

fun fromPixels(width: Int, height: Int, pixels: List<Pixel>): Image {
    val image = WritableImage(width, height)
    var index = 0

    for (y in 0 until height) {
        for (x in 0 until width) {
            image.pixelWriter.setColor(x, y, pixels[index++].color)
        }
    }

    return image
}

/**
 * Merges given [images] horizontally into a single [Image].
 */
fun merge(images: List<Image>): Image {
    if (images.isEmpty())
        return getDummyImage()

    if (images.size == 1)
        return images.first()

    val width = images.sumBy { it.width.toInt() }
    val height = images.map { it.height.toInt() }.maxOrNull() ?: 0

    val image = WritableImage(width, height)

    var dstX = 0

    images.forEach {
        val w = it.width.toInt()
        val h = it.height.toInt()

        image.pixelWriter.setPixels(dstX, 0, w, h, it.pixelReader, 0, 0)

        dstX += w
    }

    return image
}

/**
 * Resize [image] without preserving ratio.
 */
fun resize(image: Image, targetWidth: Int, targetHeight: Int): Image {
    return resize(image, targetWidth, targetHeight, false)
}

/**
 * Resize [image].
 * If [isPreserveRatio] is true, vertical scaling will be dependent on horizontal scaling and original image proportion will be preserved.
 */
fun resize(image: Image, targetWidth: Int, targetHeight: Int, isPreserveRatio: Boolean): Image {
    val width = image.width.toInt()
    val height = image.height.toInt()
    val scaleHorizontal = width.toDouble() / targetWidth
    var scaleVertical = height.toDouble() / targetHeight

    if (isPreserveRatio)
        scaleVertical = scaleHorizontal

    val output = WritableImage(targetWidth, targetHeight)

    val reader = image.pixelReader
    val writer = output.pixelWriter

    for (y in 0 until targetHeight) {
        for (x in 0 until targetWidth) {
            val argb = reader.getArgb((x * scaleHorizontal).toInt(), (y * scaleVertical).toInt())
            writer.setArgb(x, y, argb)
        }
    }

    return output
}

fun flipHorizontally(image: Image): Image {
    // indices start at 0, hence -1
    val maxX = image.width.toInt() - 1

    return image.map {
        val srcX = maxX - it.x
        val srcY = it.y
        val color = image.pixelReader.getColor(srcX, srcY)

        it.copy(color)
    }
}

fun flipVertically(image: Image): Image {
    // indices start at 0, hence -1
    val maxY = image.height.toInt() - 1

    return image.map {
        val srcX = it.x
        val srcY = maxY - it.y
        val color = image.pixelReader.getColor(srcX, srcY)

        it.copy(color)
    }
}

fun flipDiagonally(image: Image): Image {
    // indices start at 0, hence -1
    val maxX = image.width.toInt() - 1
    val maxY = image.height.toInt() - 1

    return image.map {
        val srcX = maxX - it.x
        val srcY = maxY - it.y
        val color = image.pixelReader.getColor(srcX, srcY)

        it.copy(color)
    }
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

/*
 * In blending functions below, bot is the existing color (dst)
 * and top is the new color (src).
 * In terms of textures it's top (src) drawn over bot (dst).
 */

internal val SRC_OVER_BLEND: (Pixel, Pixel) -> Pixel = { bot, top ->
    if (top.color == Color.TRANSPARENT) {
        bot.copy(Color.TRANSPARENT)
    } else {
        val color = newColor(
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
        val color = newColor(
                top.R * bot.A + bot.R * (1 - top.R),
                top.G * bot.A + bot.G * (1 - top.G),
                top.B * bot.A + bot.B * (1 - top.B),
                bot.A
        )

        bot.copy(color)
    }
}

internal val ADD_BLEND: (Pixel, Pixel) -> Pixel = { bot, top ->
    if (top.color == Color.TRANSPARENT) {
        bot.copy(Color.TRANSPARENT)
    } else {
        val color = newColor(
                minOf(1.0, bot.color.red + top.color.red),
                minOf(1.0, bot.color.green + top.color.green),
                minOf(1.0, bot.color.blue + top.color.blue),
                minOf(1.0, bot.color.opacity + top.color.opacity)
        )

        bot.copy(color)
    }
}

internal val MULTIPLY_BLEND: (Pixel, Pixel) -> Pixel = { bot, top ->
    if (top.color == Color.TRANSPARENT) {
        bot.copy(Color.TRANSPARENT)
    } else {
        val color = newColor(
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
        val color = newColor(
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

        val color = newColor(
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
        val color = newColor(
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
        val color = newColor(
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
        val color = newColor(
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
        val color = newColor(
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
    OVERLAY_BLEND(top, bot)
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

        val color = newColor(
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

        val color = newColor(
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

        val color = newColor(
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
        val color = newColor(
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
        val color = newColor(
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
        val color = newColor(
                bot.R,
                bot.G,
                top.B,
                top.A + bot.A * (1 - top.A)
        )

        bot.copy(color)
    }
}

private fun newColor(r: Double, g: Double, b: Double, a: Double): Color {
    return Color.color(
            max(0.0, min(1.0, r)),
            max(0.0, min(1.0, g)),
            max(0.0, min(1.0, b)),
            max(0.0, min(1.0, a))
    )
}

/**
 * @return pixel at given [x] and [y]
 */
fun Image.getPixel(x: Int, y: Int): Pixel {
    return Pixel(x, y, this.pixelReader.getColor(x, y), this)
}

/**
 * Map pixels of this image using [f] to produce a new image.
 */
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

/**
 * Given a rectangular area, produces a sub-image of this image.
 *
 * Rectangle cannot cover area outside of the original image.
 *
 * @param area area of the original texture that represents sub-image
 * @return sub-image
 */
fun Image.subImage(area: Rectangle2D): Image {
    val minX = area.minX.toInt()
    val minY = area.minY.toInt()
    val maxX = area.maxX.toInt()
    val maxY = area.maxY.toInt()

    require(minX >= 0) { "minX value of sub-image cannot be negative" }
    require(minY >= 0) { "minY value of sub-image cannot be negative" }
    require(maxX <= this.width) { "maxX value ($maxX) of sub-image cannot be greater than image width (${this.width})" }
    require(maxY <= this.height) { "maxY value ($maxY) of sub-image cannot be greater than image height (${this.height})" }

    val pixelReader = this.pixelReader
    val newImage = WritableImage(maxX - minX, maxY - minY)

    newImage.pixelWriter.setPixels(0, 0, newImage.width.toInt(), newImage.height.toInt(), pixelReader, minX, minY)

    return newImage
}

fun Image.toByteArray(): ByteArray {
    val awtImage = toBufferedImage(this)

    val baos = ByteArrayOutputStream()

    ImageIO.write(awtImage, "png", baos)

    return baos.toByteArray()
}

fun Image.toBase64(): String {
    return Base64.getEncoder().encodeToString(this.toByteArray())
}

/**
 * @return the sum of rgb values, which is in range [0..3]
 */
fun Color.rgbSum(): Double = this.red + this.green + this.blue

fun ByteArray.toImage(): Image {
    val parent = WritableImage(2, 2)
    return fromPixels(39, 38,
            this.map { if (it == 0.toByte()) Color.BLACK else Color.WHITE }
            .map { Pixel(0, 0, it, parent) }
    )
}

fun toBufferedImage(fxImage: Image): java.awt.image.BufferedImage {
    val w = fxImage.width.toInt()
    val h = fxImage.height.toInt()

    // empty AWT image
    val awtImage = java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB_PRE)
    val buffer = awtImage.raster.dataBuffer as java.awt.image.DataBufferInt

    // copy fx image data to AWT image buffer
    fxImage.pixelReader.getPixels(0, 0, w, h, WritablePixelFormat.getIntArgbPreInstance(), buffer.data, 0, w)

    return awtImage
}

fun fromBufferedImage(awtImage: java.awt.image.BufferedImage): Image {
    val w = awtImage.width
    val h = awtImage.height

    val buffer = awtImage.raster.dataBuffer as java.awt.image.DataBufferInt

    val fxImage = WritableImage(w, h)
    fxImage.pixelWriter.setPixels(0, 0, w, h, WritablePixelFormat.getIntArgbPreInstance(), buffer.data, 0, w)

    return fxImage
}

/**
 * Writes [image] to the [filePath].
 */
fun writeToFile(image: Image, filePath: Path): Boolean {
    try {
        Files.newOutputStream(filePath).use {
            return ImageIO.write(toBufferedImage(image), "png", it)
        }
    } catch (e: Exception) {
        Logger.get("Images.kt").warning("failed to save", e)
        return false
    }
}

/**
 * Using given [images] interpolates between them to produce intermediate images (inbetweening).
 * The given [images] list must have at least 2 images, otherwise a new list containing the originals is returned.
 *
 * @return list with intermediate images, including the original images.
 * The new list size is (images.size - 1) * [numFramesBetweenImages] + (images.size).
 */
fun interpolateIntermediateImages(images: List<Image>, numFramesBetweenImages: Int): List<Image> {
    if (images.size < 2)
        return ArrayList(images)

    val result = arrayListOf<Image>()

    images.zipWithNext().forEach { (img1, img2) ->
        val anim = AnimatedImage(img1, img2)

        result += img1

        repeat(numFramesBetweenImages) { i ->

            // we add +1 since i == 0 will give us [img1], which we add manually, so ignore
            // and i+1 == numFramesBetweenImages will give us [img2],
            // which we also add manually during next cycle, so ignore by +1
            result += anim.getValue((i + 1) / (numFramesBetweenImages.toDouble() + 1))
        }
    }

    result += images.last()

    return result
}

/**
 * The returned value of 0 means images do not share a single pixel (x, y, color are checked).
 * The value of 1 means images are identical.
 * If images have different sizes, 0 is returned.
 *
 * @return an accuracy value [0..1] (a ratio)
 * representing the number of matched pixels over the number of total pixels
 */
fun Image.compareStrict(other: Image): Double {
    if (this.width != other.width || this.height != other.height)
        return 0.0

    val pixels0 = toPixels(this)
    val pixels1 = toPixels(other)

    val matched = pixels0.zip(pixels1)
        .count { (p0, p1) -> p0.color == p1.color }

    return matched.toDouble() / pixels0.size
}