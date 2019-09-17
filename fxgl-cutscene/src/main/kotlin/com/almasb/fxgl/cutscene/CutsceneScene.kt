/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.cutscene

import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.animation.AnimationDSL
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.input.view.KeyView
import com.almasb.fxgl.scene.SubScene
import com.almasb.fxgl.scene.SubSceneStack
import javafx.geometry.Point2D
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
class CutsceneScene(private val sceneStack: SubSceneStack, appWidth: Int, appHeight: Int) : SubScene() {

    private val animation: Animation<*>
    private val animation2: Animation<*>

    private val textRPG = Text()

    private lateinit var cutscene: Cutscene

    init {
        val topLine = Rectangle(appWidth.toDouble(), 150.0)
        topLine.translateY = -150.0

        val botLine = Rectangle(appWidth.toDouble(), 200.0)
        botLine.translateY = appHeight.toDouble()

        animation = AnimationDSL()
                .duration(Duration.seconds(0.5))
                .translate(topLine)
                .from(Point2D(0.0, -150.0))
                .to(Point2D.ZERO)
                .build()

        animation2 = AnimationDSL()
                .duration(Duration.seconds(0.5))
                .translate(botLine)
                .from(Point2D(0.0, appHeight.toDouble()))
                .to(Point2D(0.0, appHeight.toDouble() - 200.0))
                .build()

        textRPG.fill = Color.WHITE
        textRPG.font = Font.font(18.0)
        textRPG.wrappingWidth = appWidth.toDouble() - 155.0
        textRPG.translateX = 50.0
        textRPG.translateY = appHeight.toDouble() - 160.0
        textRPG.opacity = 0.0

        val keyView = KeyView(KeyCode.ENTER, Color.GREENYELLOW, 18.0)
        keyView.translateX = appWidth.toDouble() - 80.0
        keyView.translateY = appHeight - 40.0
        keyView.opacityProperty().bind(textRPG.opacityProperty())

        contentRoot.children.addAll(topLine, botLine, textRPG, keyView)

        input.addAction(object : UserAction("Next RPG Line") {
            override fun onActionBegin() {
                nextLine()
            }
        }, KeyCode.ENTER)
    }

    private fun centerTextBind(text: Text, x: Double, y: Double) {
        text.layoutBoundsProperty().addListener { _, _, bounds ->
            text.translateX = x - bounds.width / 2
            text.translateY = y - bounds.height / 2
        }
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
        textRPG.opacity = 0.0
        animation2.onFinished = Runnable {
            sceneStack.popSubScene()
            onClose()
        }
        animation.startReverse()
        animation2.startReverse()
    }

    fun start(cutscene: Cutscene) {
        this.cutscene = cutscene

        nextLine()

        sceneStack.pushSubScene(this)
    }

    private var currentLine = 0
    private lateinit var dialogLine: CutsceneDialogLine
    private val message = ArrayDeque<Char>()

    private fun nextLine() {
        // do not allow to move to next line while the text animation is going
        if (message.isNotEmpty())
            return

        if (currentLine < cutscene.lines.size) {
            dialogLine = cutscene.lines[currentLine]
            dialogLine.data.forEach { message.addLast(it) }

            textRPG.text = dialogLine.owner + ": "
            currentLine++
        } else {
            endCutscene()
        }
    }

    private fun onOpen() {
        textRPG.opacity = 1.0
    }

    private fun onClose() {
        currentLine = 0
        message.clear()
    }
}