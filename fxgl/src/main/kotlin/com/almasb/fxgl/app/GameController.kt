/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.profile.DataFile
import javafx.concurrent.Task

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface GameController {

    fun gotoIntro()

    fun gotoMainMenu()

    fun gotoGameMenu()

    /**
     * Switches the current scene to loading scene.
     * Once the given loading task is completed, the scene is switched to play.
     */
    fun gotoLoading(loadingTask: Runnable)

    /**
     * Switches the current scene to loading scene.
     * Once the given loading task is completed, the scene is switched to play.
     */
    fun gotoLoading(loadingTask: Task<*>)

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

    fun pauseEngine()

    fun resumeEngine()

    fun exit()
}

