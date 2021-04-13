/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.cutscene

import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.animation.AnimationBuilder
import com.almasb.fxgl.core.asset.AssetLoaderService
import com.almasb.fxgl.core.asset.AssetType
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.input.view.KeyView
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.scene.SceneService
import com.almasb.fxgl.scene.SubScene
import javafx.geometry.Point2D
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.util.Duration
import java.util.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class CutsceneScene(private val sceneService: SceneService) : SubScene() {

    private val log = Logger.get<CutsceneScene>()

    private val speakers = arrayListOf<Speaker>()

    private val animation: Animation<*>
    private val animation2: Animation<*>

    private val speakerImageView = ImageView()
    private val textRPG = Text()

    internal lateinit var assetLoader: AssetLoaderService
    private lateinit var cutscene: Cutscene

    init {
        val topLine = Rectangle(sceneService.prefWidth, 150.0)
        topLine.translateY = -150.0

        val botLine = Rectangle(sceneService.prefWidth, 220.0)
        botLine.translateY = sceneService.prefHeight

        animation = AnimationBuilder()
                .duration(Duration.seconds(0.5))
                .translate(topLine)
                .from(Point2D(0.0, -150.0))
                .to(Point2D.ZERO)
                .build()

        animation2 = AnimationBuilder()
                .duration(Duration.seconds(0.5))
                .translate(botLine)
                .from(Point2D(0.0, sceneService.prefHeight))
                .to(Point2D(0.0, sceneService.prefHeight - 220.0))
                .build()

        speakerImageView.translateX = 25.0
        speakerImageView.translateY = sceneService.prefHeight - 220.0 + 10
        speakerImageView.opacity = 0.0

        textRPG.fill = Color.WHITE
        textRPG.font = Font.font(18.0)
        textRPG.wrappingWidth = sceneService.prefWidth - 155.0 - 200
        textRPG.translateX = 250.0
        textRPG.translateY = sceneService.prefHeight - 160.0
        textRPG.opacity = 0.0

        val keyView = KeyView(KeyCode.ENTER, Color.GREENYELLOW, 18.0)
        keyView.translateX = sceneService.prefWidth - 80.0
        keyView.translateY = sceneService.prefHeight - 40.0
        keyView.opacityProperty().bind(textRPG.opacityProperty())

        contentRoot.children.addAll(topLine, botLine, speakerImageView, textRPG, keyView)

        input.addAction(object : UserAction("Next RPG Line") {
            override fun onActionBegin() {
                nextLine()
            }
        }, KeyCode.ENTER)
    }

    override fun onCreate() {
        animation2.onFinished = Runnable {
            onOpen()
        }

        animation.start()
        animation2.start()
    }

    override fun onUpdate(tpf: Double) {
        animation.onUpdate(tpf)
        animation2.onUpdate(tpf)

        if (message.isNotEmpty()) {
            textRPG.text += message.poll()
        }
    }

    private fun endCutscene() {
        speakerImageView.image = null
        speakerImageView.opacity = 0.0
        textRPG.opacity = 0.0

        animation2.onFinished = Runnable {
            sceneService.popSubScene()
            onClose()
        }
        animation.startReverse()
        animation2.startReverse()
    }

    fun start(cutscene: Cutscene) {
        this.cutscene = cutscene

        nextLine()

        sceneService.pushSubScene(this)
    }

    private var currentLine = 0
    private val message = ArrayDeque<Char>()

    private fun nextLine() {
        // do not allow to move to next line while the text animation is going
        if (message.isNotEmpty())
            return

        if (currentLine == cutscene.lines.size) {
            endCutscene()
            return
        }

        val line = cutscene.lines[currentLine].trim()

        currentLine++

        try {
            parseLine(line)
        } catch (e: Exception) {
            log.warning("Cannot parse, skipping: $line", e)
            nextLine()
        }
    }

    private fun parseLine(line: String) {
        if (line.startsWith("//") || line.startsWith("#") || line.isEmpty()) {
            // skip line if comment
            nextLine()
            return
        }

        for (i in line.indices) {
            val c = line[i]

            if (c == '.') {
                // parse init
                val id = line.substring(0, i)
                val speaker = speakers.find { it.id == id }
                        ?: Speaker(id).also { speakers += it }

                val subLine = line.substring(i+1)
                val indexEquals = subLine.indexOf('=')

                val varName = subLine.substring(0, indexEquals).trim()
                val varValue = subLine.substring(indexEquals+1).trim()

                when (varName) {
                    "name" -> { speaker.name = varValue }
                    "image" -> { speaker.imageName = varValue }
                }

                nextLine()
                return
            }

            if (c == ':') {
                // parse line of text
                val id = line.substring(0, i)
                val text = line.substring(i+1)

                text.forEach { message.addLast(it) }

                val speaker = speakers.find { it.id == id }
                        ?: Speaker(id).also { speakers += it }

                textRPG.text = "${speaker.name}: "

                if (speaker.imageName.isEmpty()) {
                    speakerImageView.image = null
                } else {
                    val image = assetLoader.load<Image>(AssetType.IMAGE, speaker.imageName)
                    speakerImageView.image = image
                }

                return
            }
        }
    }

    private fun onOpen() {
        speakerImageView.opacity = 1.0
        textRPG.opacity = 1.0
    }

    private fun onClose() {
        currentLine = 0
        message.clear()
    }
}

private class Speaker(
        val id: String,
        var name: String = "",
        var imageName: String = ""
)

/**
 * A cutscene is constructed using a list of lines either read from a .txt file
 * or produced dynamically. The format is defined in
 * https://github.com/AlmasB/FXGL/wiki/Narrative-and-Dialogue-System-(FXGL-11)#cutscenes
 */
class Cutscene(val lines: List<String>)

interface CutsceneCallback {

    // fun onNextLine()

    fun onCutsceneEnded() {

    }
}

internal class CutsceneParser(val cutscene: Cutscene) {

}