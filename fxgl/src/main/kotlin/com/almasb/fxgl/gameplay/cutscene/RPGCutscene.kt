/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.cutscene

import com.almasb.fxgl.parser.JavaScriptParser
import java.util.function.Supplier

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class RPGCutscene(val scriptName: String) {

    private val js: JavaScriptParser = JavaScriptParser(scriptName)

    val playerLines: List<RPGDialogLine>
    val npcLines: List<RPGDialogLine>

    init {
        playerLines = loadLines("playerLinesWrap").plus(RPGDialogLine(999, "Bye!"))

        npcLines = loadLines("npcLinesWrap")
    }

    private fun loadLines(funcName: String): List<RPGDialogLine> {
        return js.callFunction<Array<Any>>(funcName)
                .map { "$it" }
                .map {
                    val id = it.toCharArray()[0].toString().toInt()
                    val data = it.substring(2)

                    val line = RPGDialogLine(id, data)
                    line.precondition = Supplier<Boolean> { js.callFunction<Boolean>("precond", line.id) }
                    line.postAction = Runnable { js.callFunction<Void>("npcActions", line.id) }

                    line
                }
    }

    internal fun playerSelected(line: RPGDialogLine): RPGDialogLine {
        val npcLineID = js.callFunction<Int>("mapLines", line.id)

        return npcLines.find { it.id == npcLineID } ?: RPGDialogLine.END
    }
}