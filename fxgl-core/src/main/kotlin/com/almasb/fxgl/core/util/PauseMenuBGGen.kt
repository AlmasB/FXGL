/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

//package com.almasb.fxgl.core.util
//
//import javafx.embed.swing.SwingFXUtils
//import javafx.scene.SnapshotParameters
//import javafx.scene.effect.DropShadow
//import javafx.scene.paint.Color
//import javafx.scene.shape.Polygon
//import javafx.scene.shape.StrokeType
//import java.nio.file.Files
//import java.nio.file.Paths
//import javax.imageio.ImageIO
//
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
//    val img = SwingFXUtils.fromFXImage(image, null)
//
//    try {
//        Files.newOutputStream(Paths.get("pause_menu_bg.png")).use {
//            ImageIO.write(img, "png", it)
//        }
//    } catch (e: Exception) {
//        println("failed $e")
//    }
//}