/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.profile.SaveFile
import com.almasb.fxgl.scene.SubSceneStack

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface GameController : SubSceneStack {

    fun startNewGame()

    fun gotoIntro()

    fun gotoMainMenu()

    fun gotoGameMenu()

    fun gotoPlay()

    fun saveGame(saveFile: SaveFile)

    fun loadGame(saveFile: SaveFile)

    fun loadGameFromLastSave()

    fun onGameReady(vars: PropertyMap)

    fun exit()

    fun saveScreenshot(): Boolean

    fun restoreDefaultSettings()
}

