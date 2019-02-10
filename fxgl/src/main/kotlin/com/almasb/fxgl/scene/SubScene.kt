/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene

import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.layout.Pane

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
open class SubScene : Scene() {

    val view: Node = Pane()

    protected val children: ObservableList<Node>
        get() = (view as Pane).children
}