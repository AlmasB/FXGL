/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.cutscene

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.centerTextBind
import com.almasb.fxgl.input.UserAction
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import java.util.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class JRPGCutsceneState : CutsceneState() {

    private val textRPG = Text()

    private lateinit var cutscene: JRPGCutscene

    init {
        textRPG.fill = Color.WHITE
        textRPG.font = Font.font(18.0)
        textRPG.wrappingWidth = FXGL.getAppWidth().toDouble() - 50.0
        textRPG.translateX = 50.0
        textRPG.translateY = FXGL.getAppHeight() - 100.0

        centerTextBind(textRPG, FXGL.getAppWidth() / 2.0, FXGL.getAppHeight() - 100.0)

        children.addAll(textRPG)

        input.addAction(object : UserAction("Next RPG Line") {
            override fun onActionBegin() {
                nextRPGLine()
            }
        }, KeyCode.ENTER)
    }

    override fun onOpen() {

    }

    override fun onClose() {
        currentLine = 0
        message.clear()
    }

    internal fun start(cutscene: JRPGCutscene) {
        this.cutscene = cutscene

        nextRPGLine()
    }

    private var currentLine = 0
    private lateinit var dialogLine: JRPGDialogLine
    private val message = ArrayDeque<Char>()

    private fun nextRPGLine() {
        if (currentLine < cutscene.lines.size) {
            dialogLine = cutscene.lines[currentLine]
            dialogLine.data.forEach { message.addLast(it) }

            textRPG.text = dialogLine.owner + ": "
            currentLine++
        } else {
            endCutscene()
        }
    }

    override fun onUpdate(tpf: Double) {
        if (message.isNotEmpty()) {
            textRPG.text += message.poll()
        }
    }
}