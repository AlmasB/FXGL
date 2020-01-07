/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.profile

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface SaveLoadHandler {

    fun onSave(dataFile: DataFile)

    fun onLoad(dataFile: DataFile)
}