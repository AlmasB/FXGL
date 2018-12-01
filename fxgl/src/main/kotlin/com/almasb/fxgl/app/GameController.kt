/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.saving.DataFile
import com.almasb.fxgl.saving.SaveFile

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface GameController {

    fun startNewGame()

    fun gotoMainMenu()

    fun gotoGameMenu()

    fun gotoPlay()

    fun saveGame(fileName: String)

    fun loadGame(saveFile: SaveFile)

    fun loadGameFromLastSave()

    fun exit()


    fun saveScreenshot(): Boolean

    fun fixAspectRatio()
}