/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.profile.DataFile
import com.almasb.fxgl.scene.SubSceneStack

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface GameController : SubSceneStack {

    fun gotoIntro()

    fun gotoMainMenu()

    fun gotoGameMenu()

    fun gotoPlay()

    fun startNewGame()

    /**
     * Saves game data into given data file.
     * This method does not write to file system.
     */
    fun saveGame(dataFile: DataFile)

    /**
     * Loads game data from given data file.
     * This method does not read from the file system.
     */
    fun loadGame(dataFile: DataFile)

    fun onGameReady(vars: PropertyMap)

    fun exit()

    fun saveScreenshot(): Boolean

    fun restoreDefaultSettings()
}

