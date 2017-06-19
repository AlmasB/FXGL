/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.cutscene

import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.State
import com.almasb.fxgl.app.SubState
import javafx.beans.binding.Bindings
import javafx.geometry.Point2D
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.util.Duration

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class CutsceneState : SubState() {

    private val textNPC = Text()
    private val boxPlayerLines = VBox(3.0)
    private val uiIDtoDialogLine = hashMapOf<Int, DialogLine>()

    private lateinit var cutscene: Cutscene

    private val animation: Animation<*>
    private val animation2: Animation<*>

    init {
        val topLine = Rectangle(FXGL.getAppWidth().toDouble(), 150.0)
        topLine.translateY = -150.0

        val botLine = Rectangle(FXGL.getAppWidth().toDouble(), 200.0)
        botLine.translateY = FXGL.getAppHeight().toDouble()

        boxPlayerLines.translateX = 10.0
        boxPlayerLines.translateYProperty().bind(botLine.translateYProperty().add(10))
        boxPlayerLines.isVisible = false

        textNPC.fill = Color.WHITE
        textNPC.font = Font.font(18.0)
        FXGL.getUIFactory().centerTextBind(textNPC, FXGL.getAppWidth() / 2.0, 75.0)

        children.addAll(topLine, botLine, textNPC, boxPlayerLines)

        animation = FXGL.getUIFactory().translate(topLine, Point2D.ZERO, Duration.seconds(0.5))
        animation2 = FXGL.getUIFactory().translate(botLine, Point2D(0.0, FXGL.getAppHeight() - 200.0), Duration.seconds(0.5))
    }

    override fun onEnter(prevState: State?) {
        animation2.onFinished = Runnable {
            boxPlayerLines.isVisible = true
        }
        animation.start(this)
        animation2.start(this)
    }

    private fun populatePlayerLines() {
        uiIDtoDialogLine.clear()
        boxPlayerLines.children.clear()
        var idUI = 1

        for (line in cutscene.playerLines) {

            if (!line.isAvailable()) {
                continue
            }

            val text = FXGL.getUIFactory().newText("$idUI. ${line.data}", 18.0)
            text.font = Font.font(18.0)
            text.fillProperty().bind(
                    Bindings.`when`(text.hoverProperty())
                            .then(Color.YELLOW)
                            .otherwise(Color.WHITE)
            )

            val id = idUI

            text.setOnMouseClicked {
                selectLine(id)
            }

            boxPlayerLines.children.add(text)

            uiIDtoDialogLine[idUI] = line

            idUI++
        }
    }

    private fun selectLine(idUI: Int) {
        val playerLine = uiIDtoDialogLine[idUI]!!

        val npcLine = cutscene.playerSelected(playerLine)

        if (npcLine.isEnd()) {
            endCutscene()
        } else {
            textNPC.text = npcLine.data
            npcLine.postAction.run()
        }
    }

    internal fun start(cutscene: Cutscene) {
        this.cutscene = cutscene

        populatePlayerLines()
    }

    private fun endCutscene() {
        boxPlayerLines.isVisible = false
        animation2.onFinished = Runnable {
            FXGL.getApp().stateMachine.popState()
        }
        animation.startReverse(this)
        animation2.startReverse(this)
    }
}