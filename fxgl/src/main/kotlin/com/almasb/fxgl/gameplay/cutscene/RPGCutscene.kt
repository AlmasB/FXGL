/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.cutscene

import java.util.*

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class RPGCutscene {

    internal val lines: List<RPGDialogLine>

    init {
        // TODO: hardcoded

        val dialogLines = Arrays.asList(
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
                "and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s,",
                "when an unknown printer took a galley of type and scrambled it to make a type",
                "specimen book. It has survived not only five centuries, but also the leap into ",
                "electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages"
        )

        lines = dialogLines.map { RPGDialogLine("NPC", it) }
    }
}