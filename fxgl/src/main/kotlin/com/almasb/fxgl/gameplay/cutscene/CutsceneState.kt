/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.cutscene

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.SubState
import com.almasb.fxgl.parser.JavaScriptParser
import javafx.scene.layout.VBox
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import jdk.nashorn.api.scripting.ScriptUtils
import java.util.function.Supplier

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class CutsceneState(scriptName: String) : SubState() {

    private val boxPlayerLines = VBox(3.0)
    private val uiIDtoDialogID = hashMapOf<Int, Int>()

    private val playerLines = arrayListOf<DialogLine>()
    private val npcLines = arrayListOf<DialogLine>()

    private val mapLines = hashMapOf<Int, Int>()
    private val preconditions = hashMapOf<Int, Supplier<Boolean>>()

    private val parser: JavaScriptParser

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

        parser.callFunction<Void>("mapLines", mapLines)
//        parser.callFunction<Void>("mapPreconditions", preconditions)
//
//        for ((id, func) in preconditions) {
//            playerLines[id-1].precondition = func
//        }


        val topLine = Rectangle(FXGL.getAppWidth().toDouble(), 150.0)
        val botLine = Rectangle(FXGL.getAppWidth().toDouble(), 200.0)
        botLine.translateY = FXGL.getAppHeight() - 200.0

        boxPlayerLines.translateX = 10.0
        boxPlayerLines.translateY = botLine.translateY + 10

        children.addAll(topLine, botLine, boxPlayerLines)

        populatePlayerLines()
    }

    private fun populatePlayerLines() {
        uiIDtoDialogID.clear()
        var idUI = 1

        for (line in playerLines) {

            if (!parser.callFunction<Boolean>("precond", line.id)) {
            //if (!line.precondition.get()) {
                continue
            }

            val text = FXGL.getUIFactory().newText("$idUI. ${line.data}", 18.0)
            text.font = Font.font(18.0)

            val id = idUI

            text.setOnMouseClicked {
                selectLine(id)
                FXGL.getApp().stateMachine.popState()
            }

            boxPlayerLines.children.add(text)

            uiIDtoDialogID[idUI] = line.id

            idUI++
        }
    }

    private fun selectLine(idUI: Int) {
        println("Selected: $idUI")

        uiIDtoDialogID[idUI]?.let {
            println(it)
        }
    }
}