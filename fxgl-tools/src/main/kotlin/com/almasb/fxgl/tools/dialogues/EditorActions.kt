/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

interface EditorAction {

    fun run()

    fun undo()
}

// possible actions:
// * move node
// * add node
// * remove node (and its incident edges)
// * add edge
// * remove edge