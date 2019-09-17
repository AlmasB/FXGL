/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.cutscene

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Cutscene(lines: List<String>) {

    val lines = lines.map {
        val index = it.indexOf(":")

        val owner = it.substring(0, index).trim()
        val text = it.substring(index + 1).trim()

        CutsceneDialogLine(owner, text)
    }
}