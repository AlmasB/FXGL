/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev.tools

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ImageUtils {

    companion object {

        @JvmStatic fun save(fxImage: Image): Boolean {
            val img = SwingFXUtils.fromFXImage(fxImage, null)

            var fileName = "file.png"

            try {
                val name = if (fileName.endsWith(".png")) fileName else "$fileName.png"

                Files.newOutputStream(Paths.get(name)).use {
                    return ImageIO.write(img, "png", it)
                }
            } catch (e: Exception) {
                println("Failed $e")
                return false
            }
        }
    }
}