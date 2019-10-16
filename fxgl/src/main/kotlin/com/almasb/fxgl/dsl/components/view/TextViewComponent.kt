/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components.view

import javafx.scene.text.Text

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TextViewComponent(x: Double, y: Double, val message: String) : ChildViewComponent(x, y, isTransformApplied = false) {

    val text = Text(message)

    init {
        viewRoot.children += text
    }
}