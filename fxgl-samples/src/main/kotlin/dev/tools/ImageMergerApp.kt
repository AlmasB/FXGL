/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev.tools

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.GameSettings
import com.almasb.fxgl.dsl.addUINode
import com.almasb.fxgl.texture.Texture
import com.almasb.fxgl.texture.getDummyImage
import javafx.geometry.HorizontalDirection
import javafx.geometry.VerticalDirection
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.stage.FileChooser
import java.io.File
import java.lang.Exception
import java.util.*
import java.util.stream.Collectors


/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ImageMergerApp : GameApplication() {

    private val files = ArrayList<File>()
    private var fieldFramesPerRow: TextField? = null

    private var lastOpenedDirectory: File? = null

    override fun initSettings(settings: GameSettings) { }

    override fun initUI() {
        val btnClear = Button("Clear")
        btnClear.setOnAction { files.clear() }

        addUINode(btnClear, 200.0, 0.0)

        val btnStart = Button("Start")
        btnStart.setOnAction { start() }

        fieldFramesPerRow = TextField()
        fieldFramesPerRow!!.promptText = "Frames per row"

        addUINode(fieldFramesPerRow!!, 20.0, 200.0)

        addUINode(btnStart, 100.0, 0.0)

        val btnSelectFiles = Button("Select...")
        btnSelectFiles.setOnAction {
            val chooser = FileChooser()

            lastOpenedDirectory?.let {
                chooser.initialDirectory = it
            }

            chooser.showOpenMultipleDialog(btnStart.scene.window)?.let {
                lastOpenedDirectory = it.first().parentFile

                files.clear()
                files.addAll(it)
            }
        }

        addUINode(btnSelectFiles, 0.0, 0.0)
    }

    private fun start() {
        val text = fieldFramesPerRow!!.text

        if (text.isEmpty()) {
            val textures = files.parallelStream().map { f -> loadTexture(f) }.collect(Collectors.toList())

            if (textures.size > 1) {
                var texture = textures[0]

                for (i in 1 until textures.size) {
                    texture = texture.superTexture(textures[i], HorizontalDirection.RIGHT)
                }

                ImageUtils.save(texture.image)
            }

        } else {

            val framesPerRow = text.toInt()

            // assume num files > framesPerRow

            val textures = files.parallelStream().map { f -> loadTexture(f) }.collect(Collectors.toList())

            val texturesPerRow = arrayListOf<Texture>()


            textures.groupBy { textures.indexOf(it) / framesPerRow }.forEach { rowNum, texturesInRow ->
                var texture = texturesInRow[0]

                for (i in 1 until texturesInRow.size) {
                    texture = texture.superTexture(texturesInRow[i], HorizontalDirection.RIGHT)
                }

                texturesPerRow += texture
            }

            var texture = texturesPerRow[0]

            for (i in 1 until texturesPerRow.size) {
                texture = texture.superTexture(texturesPerRow[i], VerticalDirection.DOWN)
            }

            ImageUtils.save(texture.image)
        }
    }

    private fun loadTexture(file: File): Texture {
        try {
            return Texture(Image(file.toURI().toURL().toExternalForm()))
        } catch (e: Exception) {
            return Texture(getDummyImage())
        }
    }
}

fun main(args: Array<String>) {
    GameApplication.launch(ImageMergerApp::class.java, args)
}