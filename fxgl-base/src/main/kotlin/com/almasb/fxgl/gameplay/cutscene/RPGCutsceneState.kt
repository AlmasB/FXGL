/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.cutscene

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.centerTextBind
import javafx.beans.binding.Bindings
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text

/**
* @author Almas Baimagambetov (almaslvl@gmail.com)
*/
internal class RPGCutsceneState : CutsceneState() {

    private val textNPC = Text()
    private val boxPlayerLines = VBox(3.0)

    private val uiIDtoDialogLine = hashMapOf<Int, RPGDialogLine>()

    private lateinit var cutscene: RPGCutscene

    init {
        boxPlayerLines.translateX = 10.0
        boxPlayerLines.translateY = FXGL.getAppHeight() - 190.0
        boxPlayerLines.isVisible = false

        textNPC.translateY = 50.0
        textNPC.fill = Color.WHITE
        textNPC.font = Font.font(18.0)
        textNPC.wrappingWidth = FXGL.getAppWidth() - 100.0
        centerTextBind(textNPC, FXGL.getAppWidth() / 2.0, 75.0)

        children.addAll(textNPC, boxPlayerLines)
    }

    override fun onOpen() {
        boxPlayerLines.isVisible = true
    }

    override fun onClose() {
        boxPlayerLines.isVisible = false
    }

    internal fun start(cutscene: RPGCutscene) {
        this.cutscene = cutscene

        populatePlayerLines()
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
}