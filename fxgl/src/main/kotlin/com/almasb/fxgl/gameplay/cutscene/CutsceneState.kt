/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.cutscene

import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.PauseMenuSubState
import com.almasb.fxgl.app.State
import com.almasb.fxgl.app.SubState
import com.almasb.fxgl.parser.JavaScriptParser
import com.almasb.fxgl.util.EmptyRunnable
import javafx.beans.binding.Bindings
import javafx.geometry.Point2D
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.util.Duration
import java.util.function.Supplier

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class CutsceneState(scriptName: String) : SubState() {

    private val textNPC = Text()
    private val boxPlayerLines = VBox(3.0)
    private val uiIDtoDialogID = hashMapOf<Int, Int>()

    private val playerLines = arrayListOf<DialogLine>()
    private val npcLines = arrayListOf<DialogLine>()

    private val mapLines = hashMapOf<Int, Int>()
    private val preconditions = hashMapOf<Int, Supplier<Boolean>>()

    private val parser: JavaScriptParser

    private val animation: Animation<*>
    private val animation2: Animation<*>

    init {
        parser = JavaScriptParser(scriptName)

        val array = parser.callFunction<Array<Any>>("playerLinesWrap").map { "$it" }
        array.forEach {
            val id = it.toCharArray()[0].toString().toInt()
            val data = it.substring(2)

            playerLines.add(DialogLine(id, data))
        }

        playerLines.add(DialogLine(playerLines.size + 1, "Bye!"))

        parser.callFunction<Array<Any>>("npcLinesWrap").map { "$it" }.forEach {
            val id = it.toCharArray()[0].toString().toInt()
            val data = it.substring(2)

            npcLines.add(DialogLine(id, data))
        }

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

        populatePlayerLines()

        animation = FXGL.getUIFactory().translate(topLine, Point2D.ZERO, Duration.seconds(0.5))
        animation2 = FXGL.getUIFactory().translate(botLine, Point2D(0.0, FXGL.getAppHeight() - 200.0), Duration.seconds(0.5))
    }

    override fun onEnter(prevState: State?) {
        animation.onFinished = Runnable {
            boxPlayerLines.isVisible = true
        }
        animation.start(this)
        animation2.start(this)
    }

    private fun populatePlayerLines() {
        uiIDtoDialogID.clear()
        var idUI = 1

        for (line in playerLines) {

            if (!parser.callFunction<Boolean>("precond", line.id)) {
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

            uiIDtoDialogID[idUI] = line.id

            idUI++
        }
    }

    private fun selectLine(idUI: Int) {
        val playerLineID = uiIDtoDialogID[idUI]

        val npcLineID = parser.callFunction<Int>("mapLines", playerLineID)

        println("$playerLineID $npcLineID")

        npcLines.forEach { println(it.id) }

        if (npcLineID == 0) {
            boxPlayerLines.isVisible = false
            animation.onFinished = Runnable {
                FXGL.getApp().stateMachine.popState()
            }
            animation.startReverse(this)
            animation2.startReverse(this)
        } else {
            textNPC.text = npcLines.find { it.id == npcLineID }?.data

            parser.callFunction<Void>("npcActions", npcLineID)
        }
    }
}