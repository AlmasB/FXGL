/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.cutscene

import com.almasb.fxgl.app.FXGL

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class JRPGCutscene(val scriptName: String) {

    internal val lines: List<JRPGDialogLine>

    init {
        lines = FXGL.getAssetLoader().loadText(scriptName).map {
            val index = it.indexOf(":")

            val owner = it.substring(0, index).trim()
            val text = it.substring(index + 1).trim()

            JRPGDialogLine(owner, text)
        }
    }
}