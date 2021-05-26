/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui

import javafx.scene.Parent

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class UI(val root: Parent, private val controller: UIController) {

    @Suppress("UNCHECKED_CAST")
    fun <T : UIController> getController(): T {
        return controller as T
    }
}