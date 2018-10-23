/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui

/**
 * Marks a class that it's able to act as a JavaFX controller.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
interface UIController {

    /**
     * Called after FXML fields injection.
     */
    fun init()
}