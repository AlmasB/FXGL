/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.util
//
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.zip.InflaterInputStream

//
//import javafx.scene.SnapshotParameters
//import javafx.scene.effect.DropShadow
//import javafx.scene.paint.Color
//import javafx.scene.shape.Polygon
//import javafx.scene.shape.StrokeType
//import java.nio.file.Files
//import java.nio.file.Paths
//import javax.imageio.ImageIO
//
object PauseMenuBGGen {
    private const val GEN_KEY = "eNqdlAsOgCAMQx/3v7RGwRTdp5EQs0CZY20ZwxicE564hF5jgJUWM1uLakuzCuqvF8AoSmKOGBbtoesLtNGAhAp8FUTcouKI7E0G0RLmnDsK2ypd36qVqKBM3dXkdzBc/g0RmLlu2WLay/LK58fBQkhv76E/vSDvSSTfQBXwVXFql39O9c6odhOvbhEJawJLriww5J0In6ibtvWgQkb6tXgA+4UEzw=="

    fun generate(): ByteArray {
        val bytes = Base64.getDecoder().decode(GEN_KEY)

        val baos = ByteArrayOutputStream()

        InflaterInputStream(bytes.inputStream()).use {
            it.copyTo(baos)
        }

        return baos.toByteArray()
    }
}
///**
// *
// *
// * @author Almas Baimagambetov (almaslvl@gmail.com)
// */
//fun main(args: Array<String>) {
//    val dx = 20.0
//    val dy = 15.0
//    val width = 250.0
//    val height = 400.0
//
//    val outer = Polygon(
//            0.0, dx,
//            dx, 0.0,
//            width * 2 / 3, 0.0,
//            width * 2 / 3, 2 * dy,
//            width * 2 / 3 + 3 * dx, 2 * dy,
//            width * 2 / 3 + 3 * dx, 0.0,
//            width - dx, 0.0,
//            width, dx,
//            width, height - dy,
//            width - dy, height,
//            dy, height,
//            0.0, height - dy,
//            0.0, height / 3 + 4 * dy,
//            dx, height / 3 + 5 * dy,
//            dx, height / 3 + 3 * dy,
//            0.0, height / 3 + 2 * dy,
//            0.0, height / 3,
//            dx, height / 3 + dy,
//            dx, height / 3 - dy,
//            0.0, height / 3 - 2 * dy
//    )
//
//    outer.fill = Color.BLACK
//    outer.stroke = Color.AQUA
//    outer.strokeWidth = 6.0
//    outer.strokeType = StrokeType.CENTERED
//    outer.effect = DropShadow(15.0, Color.BLACK)
//
//    val params = SnapshotParameters()
//    params.fill = Color.TRANSPARENT
//
//    // draw into image to speed up rendering
//    val image = outer.snapshot(params, null)
//
//    val img = ImageIO.read(ImageIO.createImageInputStream(fxImage))
//
//    try {
//        Files.newOutputStream(Paths.get("pause_menu_bg.png")).use {
//            ImageIO.write(img, "png", it)
//        }
//    } catch (e: Exception) {
//        println("failed $e")
//    }
//}