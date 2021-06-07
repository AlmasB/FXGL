/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.texture

import com.almasb.fxgl.test.RunWithFX
import javafx.geometry.Rectangle2D
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(RunWithFX::class)
class NineSliceTextureBuilderTest {

    private val smallSize = 300.0
    private val baseSize = 525.0
    private val bigSize = 600.0

    private lateinit var smallTexture: Texture
    private lateinit var baseTexture: Texture
    private lateinit var bigTexture: Texture
    private lateinit var horizontalTexture: Texture
    private lateinit var verticalTexture: Texture

    private lateinit var builder: NineSliceTextureBuilder

    @BeforeEach
    fun setUp() {
        smallTexture = Texture(Image(javaClass.getResource("SliceSampleSmall.png").toExternalForm()))
        baseTexture = Texture(Image(javaClass.getResource("SliceSampleBase.png").toExternalForm()))
        bigTexture = Texture(Image(javaClass.getResource("SliceSampleBig.png").toExternalForm()))
        horizontalTexture = Texture(Image(javaClass.getResource("SliceSampleHorizontal.png").toExternalForm()))
        verticalTexture = Texture(Image(javaClass.getResource("SliceSampleVertical.png").toExternalForm()))

        builder = createBuilder(baseTexture)
    }

    @Test
    fun `Set textures directly`() {
        val manualBuilder = NineSliceTextureBuilder(ColoredTexture(1, 1, Color.BLACK))

        // black corners
        manualBuilder.topLeft = ColoredTexture(1, 1, Color.BLACK)
        manualBuilder.botLeft = ColoredTexture(1, 1, Color.BLACK)
        manualBuilder.topRight = ColoredTexture(1, 1, Color.BLACK)
        manualBuilder.botRight = ColoredTexture(1, 1, Color.BLACK)

        // white sides
        manualBuilder.top = ColoredTexture(1, 1, Color.WHITE)
        manualBuilder.left = ColoredTexture(1, 1, Color.WHITE)
        manualBuilder.right = ColoredTexture(1, 1, Color.WHITE)
        manualBuilder.bot = ColoredTexture(1, 1, Color.WHITE)

        // red center
        manualBuilder.center = ColoredTexture(1, 1, Color.RED)

        assertThat(manualBuilder.topLeft.width, CoreMatchers.`is`(1.0))
        assertThat(manualBuilder.botLeft.width, CoreMatchers.`is`(1.0))
        assertThat(manualBuilder.topRight.width, CoreMatchers.`is`(1.0))
        assertThat(manualBuilder.botRight.width, CoreMatchers.`is`(1.0))

        assertThat(manualBuilder.top.width, CoreMatchers.`is`(1.0))
        assertThat(manualBuilder.left.width, CoreMatchers.`is`(1.0))
        assertThat(manualBuilder.right.width, CoreMatchers.`is`(1.0))
        assertThat(manualBuilder.bot.width, CoreMatchers.`is`(1.0))

        assertThat(manualBuilder.center.width, CoreMatchers.`is`(1.0))

        val texture = manualBuilder.build(16, 16)

        assertThat(texture.width, CoreMatchers.`is`(16.0))
        assertThat(texture.height, CoreMatchers.`is`(16.0))

        var corners = 0
        var sides = 0
        var center = 0

        texture.pixels().forEach { p ->
            if (p.x == 0 && p.y == 0
                    || p.x == 0 && p.y == 15
                    || p.x == 15 && p.y == 0
                    || p.x == 15 && p.y == 15) {

                assertThat(p.color, CoreMatchers.`is`(Color.BLACK))

                corners++

            } else if (p.x == 0 || p.y == 0 || p.x == 15 || p.y == 15) {
                assertThat(p.color, CoreMatchers.`is`(Color.WHITE))

                sides++
            } else {
                assertThat(p.color, CoreMatchers.`is`(Color.RED))

                center++
            }
        }

        assertThat(corners, CoreMatchers.`is`(4))
        assertThat(sides, CoreMatchers.`is`(56))
        assertThat(center, CoreMatchers.`is`(196))
    }

    @Test
    fun `Check loaded textures width and height`() {
        assertThat(smallTexture.width, CoreMatchers.`is`(smallSize))
        assertThat(smallTexture.height, CoreMatchers.`is`(smallSize))

        assertThat(baseTexture.width, CoreMatchers.`is`(baseSize))
        assertThat(baseTexture.height, CoreMatchers.`is`(baseSize))

        assertThat(bigTexture.width, CoreMatchers.`is`(bigSize))
        assertThat(bigTexture.height, CoreMatchers.`is`(bigSize))
    }

    @Test
    fun `Shrunk base texture matches expected`() {
        buildAndCompareTexture(smallTexture)
    }

    @Test
    fun `Enlarged base texture matches expected`() {
        buildAndCompareTexture(bigTexture)
    }

    @Test
    fun `Non-square textures are correctly generated`() {
        buildAndCompareTexture(horizontalTexture)
        buildAndCompareTexture(verticalTexture)
    }

    private fun buildAndCompareTexture(targetTexture: Texture) {
        val generatedTexture = builder.build(targetTexture.width.toInt(), targetTexture.height.toInt())

        assertThat(generatedTexture.width, CoreMatchers.`is`(targetTexture.width))
        assertThat(generatedTexture.height, CoreMatchers.`is`(targetTexture.height))

        assertThat("Colors of shrunken texture do not match with expected!", matchPixels(generatedTexture, targetTexture))
    }

    private fun createBuilder(texture: Texture): NineSliceTextureBuilder {

        return NineSliceTextureBuilder(texture)
                .topLeft(Rectangle2D(0.0, 0.0, 53.0, 53.0))
                .top(Rectangle2D(53.0, 0.0, 420.0, 53.0))
                .topRight(Rectangle2D(473.0, 0.0, 52.0, 53.0))
                .left(Rectangle2D(0.0, 52.0, 53.0, 420.0))
                .center(Rectangle2D(53.0, 52.0, 420.0, 420.0))
                .right(Rectangle2D(473.0, 52.0, 52.0, 420.0))
                .botLeft(Rectangle2D(0.0, 473.0, 53.0, 52.0))
                .botRight(Rectangle2D(473.0, 473.0, 52.0, 52.0))
                .bot(Rectangle2D(53.0, 473.0, 420.0, 52.0))
    }
}

internal fun matchPixels(tex1: Texture, tex2: Texture): Boolean {
    val pixels1 = tex1.pixels()
    val pixels2 = tex2.pixels()
    (pixels1.indices).forEach { x ->
        val pixel1 = pixels1[x]
        val pixel2 = pixels2[x]
        if (pixel1.x != pixel2.x || pixel1.y != pixel2.y // Pixel Coordinates
                || pixel1.A != pixel2.A || pixel1.B != pixel2.B || pixel1.G != pixel2.G || pixel1.R != pixel2.R // Pixel RGB A
                || pixel1.color != pixel2.color) { // Pixel Colors
            return false
        }
    }
    return true
}