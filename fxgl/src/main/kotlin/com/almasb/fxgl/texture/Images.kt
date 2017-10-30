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
        BlendMode.RED -> TODO()
        BlendMode.GREEN -> TODO()
        BlendMode.BLUE -> TODO()
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
 * A blending mode that defines the manner in which the inputs
 * are composited together.
 * Each `Mode` describes a mathematical equation that
 * combines premultiplied inputs to produce some premultiplied result.
 */
private enum class Mode {
    /**
     * The top input is blended over the bottom input.
     * (Equivalent to the Porter-Duff "source over destination" rule.)
     *
     *
     * Thus:
     * <pre>
     * *A<sub>r</sub>* = *A<sub>top</sub>* + *A<sub>bot</sub>**(1-*A<sub>top</sub>*)
     * *C<sub>r</sub>* = *C<sub>top</sub>* + *C<sub>bot</sub>**(1-*A<sub>top</sub>*)
    </pre> *
     */
    SRC_OVER,

    /**
     * The part of the top input lying inside of the bottom input
     * is kept in the resulting image.
     * (Equivalent to the Porter-Duff "source in destination" rule.)
     *
     *
     * Thus:
     * <pre>
     * *A<sub>r</sub>* = *A<sub>top</sub>***A<sub>bot</sub>*
     * *C<sub>r</sub>* = *C<sub>top</sub>***A<sub>bot</sub>*
    </pre> *
     */
    SRC_IN,

    /**
     * The part of the top input lying outside of the bottom input
     * is kept in the resulting image.
     * (Equivalent to the Porter-Duff "source held out by destination"
     * rule.)
     *
     *
     * Thus:
     * <pre>
     * *A<sub>r</sub>* = *A<sub>top</sub>**(1-*A<sub>bot</sub>*)
     * *C<sub>r</sub>* = *C<sub>top</sub>**(1-*A<sub>bot</sub>*)
    </pre> *
     */
    SRC_OUT,

    /**
     * The part of the top input lying inside of the bottom input
     * is blended with the bottom input.
     * (Equivalent to the Porter-Duff "source atop destination" rule.)
     *
     *
     * Thus:
     * <pre>
     * *A<sub>r</sub>* = *A<sub>top</sub>***A<sub>bot</sub>* + *A<sub>bot</sub>**(1-*A<sub>top</sub>*) = *A<sub>bot</sub>*
     * *C<sub>r</sub>* = *C<sub>top</sub>***A<sub>bot</sub>* + *C<sub>bot</sub>**(1-*A<sub>top</sub>*)
    </pre> *
     */
    SRC_ATOP,

    /**
     * The color and alpha components from the top input are
     * added to those from the bottom input.
     * The result is clamped to 1.0 if it exceeds the logical
     * maximum of 1.0.
     *
     *
     * Thus:
     * <pre>
     * *A<sub>r</sub>* = min(1, *A<sub>top</sub>*+*A<sub>bot</sub>*)
     * *C<sub>r</sub>* = min(1, *C<sub>top</sub>*+*C<sub>bot</sub>*)
    </pre> *
     *
     *
     * Notes:
     *
     *  * This mode is commutative (ordering of inputs
     * does not matter).
     *  * This mode is sometimes referred to as "linear dodge" in
     * imaging software packages.
     *
     */
    ADD,

    /**
     * The color components from the first input are multiplied with those
     * from the second input.
     * The alpha components are blended according to
     * the [.SRC_OVER] equation.
     *
     *
     * Thus:
     * <pre>
     * *A<sub>r</sub>* = *A<sub>top</sub>* + *A<sub>bot</sub>**(1-*A<sub>top</sub>*)
     * *C<sub>r</sub>* = *C<sub>top</sub>* * *C<sub>bot</sub>*
    </pre> *
     *
     *
     * Notes:
     *
     *  * This mode is commutative (ordering of inputs
     * does not matter).
     *  * This mode is the mathematical opposite of
     * the [.SCREEN] mode.
     *  * The resulting color is always at least as dark as either
     * of the input colors.
     *  * Rendering with a completely black top input produces black;
     * rendering with a completely white top input produces a result
     * equivalent to the bottom input.
     *
     */
    MULTIPLY,

    /**
     * The color components from both of the inputs are
     * inverted, multiplied with each other, and that result
     * is again inverted to produce the resulting color.
     * The alpha components are blended according
     * to the [.SRC_OVER] equation.
     *
     *
     * Thus:
     * <pre>
     * *A<sub>r</sub>* = *A<sub>top</sub>* + *A<sub>bot</sub>**(1-*A<sub>top</sub>*)
     * *C<sub>r</sub>* = 1 - ((1-*C<sub>top</sub>*) * (1-*C<sub>bot</sub>*))
    </pre> *
     *
     *
     * Notes:
     *
     *  * This mode is commutative (ordering of inputs
     * does not matter).
     *  * This mode is the mathematical opposite of
     * the [.MULTIPLY] mode.
     *  * The resulting color is always at least as light as either
     * of the input colors.
     *  * Rendering with a completely white top input produces white;
     * rendering with a completely black top input produces a result
     * equivalent to the bottom input.
     *
     */
    SCREEN,

    /**
     * The input color components are either multiplied or screened,
     * depending on the bottom input color.
     * The alpha components are blended according
     * to the [.SRC_OVER] equation.
     *
     *
     * Thus:
     * <pre>
     * *A<sub>r</sub>* = *A<sub>top</sub>* + *A<sub>bot</sub>**(1-*A<sub>top</sub>*)
     * REMIND: not sure how to express this succinctly yet...
    </pre> *
     *
     *
     * Notes:
     *
     *  * This mode is a combination of [.SCREEN] and
     * [.MULTIPLY], depending on the bottom input color.
     *  * This mode is the mathematical opposite of
     * the [.HARD_LIGHT] mode.
     *  * In this mode, the top input colors "overlay" the bottom input
     * while preserving highlights and shadows of the latter.
     *
     */
    OVERLAY,

    /**
     * REMIND: cross check this formula with OpenVG spec...
     *
     * The darker of the color components from the two inputs are
     * selected to produce the resulting color.
     * The alpha components are blended according
     * to the [.SRC_OVER] equation.
     *
     *
     * Thus:
     * <pre>
     * *A<sub>r</sub>* = *A<sub>top</sub>* + *A<sub>bot</sub>**(1-*A<sub>top</sub>*)
     * *C<sub>r</sub>* = min(*C<sub>top</sub>*, *C<sub>bot</sub>*)
    </pre> *
     *
     *
     * Notes:
     *
     *  * This mode is commutative (ordering of inputs
     * does not matter).
     *  * This mode is the mathematical opposite of
     * the [.LIGHTEN] mode.
     *
     */
    DARKEN,

    /**
     * REMIND: cross check this formula with OpenVG spec...
     *
     * The lighter of the color components from the two inputs are
     * selected to produce the resulting color.
     * The alpha components are blended according
     * to the [.SRC_OVER] equation.
     *
     *
     * Thus:
     * <pre>
     * *A<sub>r</sub>* = *A<sub>top</sub>* + *A<sub>bot</sub>**(1-*A<sub>top</sub>*)
     * *C<sub>r</sub>* = max(*C<sub>top</sub>*, *C<sub>bot</sub>*)
    </pre> *
     *
     *
     * Notes:
     *
     *  * This mode is commutative (ordering of inputs
     * does not matter).
     *  * This mode is the mathematical opposite of
     * the [.DARKEN] mode.
     *
     */
    LIGHTEN,

    /**
     * The bottom input color components are divided by the inverse
     * of the top input color components to produce the resulting color.
     * The alpha components are blended according
     * to the [.SRC_OVER] equation.
     *
     *
     * Thus:
     * <pre>
     * *A<sub>r</sub>* = *A<sub>top</sub>* + *A<sub>bot</sub>**(1-*A<sub>top</sub>*)
     * *C<sub>r</sub>* = *C<sub>bot</sub>* / (1-*C<sub>top</sub>*)
    </pre> *
     */
    COLOR_DODGE,

    /**
     * The inverse of the bottom input color components are divided by
     * the top input color components, all of which is then inverted
     * to produce the resulting color.
     * The alpha components are blended according
     * to the [.SRC_OVER] equation.
     *
     *
     * Thus:
     * <pre>
     * *A<sub>r</sub>* = *A<sub>top</sub>* + *A<sub>bot</sub>**(1-*A<sub>top</sub>*)
     * *C<sub>r</sub>* = 1-((1-*C<sub>bot</sub>*) / *C<sub>top</sub>*)
    </pre> *
     */
    COLOR_BURN,

    /**
     * The input color components are either multiplied or screened,
     * depending on the top input color.
     * The alpha components are blended according
     * to the [.SRC_OVER] equation.
     *
     *
     * Thus:
     * <pre>
     * *A<sub>r</sub>* = *A<sub>top</sub>* + *A<sub>bot</sub>**(1-*A<sub>top</sub>*)
     * REMIND: not sure how to express this succinctly yet...
    </pre> *
     *
     *
     * Notes:
     *
     *  * This mode is a combination of [.SCREEN] and
     * [.MULTIPLY], depending on the top input color.
     *  * This mode is the mathematical opposite of
     * the [.OVERLAY] mode.
     *
     */
    HARD_LIGHT,

    /**
     * REMIND: this is a complicated formula, TBD...
     */
    SOFT_LIGHT,

    /**
     * The darker of the color components from the two inputs are
     * subtracted from the lighter ones to produce the resulting color.
     * The alpha components are blended according
     * to the [.SRC_OVER] equation.
     *
     *
     * Thus:
     * <pre>
     * *A<sub>r</sub>* = *A<sub>top</sub>* + *A<sub>bot</sub>**(1-*A<sub>top</sub>*)
     * *C<sub>r</sub>* = abs(*C<sub>top</sub>*-*C<sub>bot</sub>*)
    </pre> *
     *
     *
     * Notes:
     *
     *  * This mode is commutative (ordering of inputs
     * does not matter).
     *  * This mode can be used to invert parts of the bottom input
     * image, or to quickly compare two images (equal pixels will result
     * in black).
     *  * Rendering with a completely white top input inverts the
     * bottom input; rendering with a completely black top input produces
     * a result equivalent to the bottom input.
     *
     */
    DIFFERENCE,

    /**
     * The color components from the two inputs are multiplied and
     * doubled, and then subtracted from the sum of the bottom input
     * color components, to produce the resulting color.
     * The alpha components are blended according
     * to the [.SRC_OVER] equation.
     *
     *
     * Thus:
     * <pre>
     * *A<sub>r</sub>* = *A<sub>top</sub>* + *A<sub>bot</sub>**(1-*A<sub>top</sub>*)
     * *C<sub>r</sub>* = *C<sub>top</sub>* + *C<sub>bot</sub>* - (2**C<sub>top</sub>***C<sub>bot</sub>*)
    </pre> *
     *
     *
     * Notes:
     *
     *  * This mode is commutative (ordering of inputs
     * does not matter).
     *  * This mode can be used to invert parts of the bottom input.
     *  * This mode produces results that are similar to those of
     * [.DIFFERENCE], except with lower contrast.
     *  * Rendering with a completely white top input inverts the
     * bottom input; rendering with a completely black top input produces
     * a result equivalent to the bottom input.
     *
     */
    EXCLUSION,

    /**
     * The red component of the bottom input is replaced with the
     * red component of the top input; the other color components
     * are unaffected.
     * The alpha components are blended according
     * to the [.SRC_OVER] equation.
     *
     *
     * Thus:
     * <pre>
     * *A<sub>r</sub>* = *A<sub>top</sub>* + *A<sub>bot</sub>**(1-*A<sub>top</sub>*)
     * *R<sub>r</sub>* = *R<sub>top</sub>*
     * *G<sub>r</sub>* = *G<sub>bot</sub>*
     * *B<sub>r</sub>* = *B<sub>bot</sub>*
    </pre> *
     */
    RED,

    /**
     * The green component of the bottom input is replaced with the
     * green component of the top input; the other color components
     * are unaffected.
     * The alpha components are blended according
     * to the [.SRC_OVER] equation.
     *
     *
     * Thus:
     * <pre>
     * *A<sub>r</sub>* = *A<sub>top</sub>* + *A<sub>bot</sub>**(1-*A<sub>top</sub>*)
     * *R<sub>r</sub>* = *R<sub>bot</sub>*
     * *G<sub>r</sub>* = *G<sub>top</sub>*
     * *B<sub>r</sub>* = *B<sub>bot</sub>*
    </pre> *
     */
    GREEN,

    /**
     * The blue component of the bottom input is replaced with the
     * blue component of the top input; the other color components
     * are unaffected.
     * The alpha components are blended according
     * to the [.SRC_OVER] equation.
     *
     *
     * Thus:
     * <pre>
     * *A<sub>r</sub>* = *A<sub>top</sub>* + *A<sub>bot</sub>**(1-*A<sub>top</sub>*)
     * *R<sub>r</sub>* = *R<sub>bot</sub>*
     * *G<sub>r</sub>* = *G<sub>bot</sub>*
     * *B<sub>r</sub>* = *B<sub>top</sub>*
    </pre> *
     */
    BLUE
}