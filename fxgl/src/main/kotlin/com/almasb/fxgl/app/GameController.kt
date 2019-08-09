/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.saving.SaveFile
import com.almasb.fxgl.saving.UserProfile
import com.almasb.fxgl.scene.SubScene
import javafx.beans.property.StringProperty

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface GameController : SceneStack {

    fun startNewGame()

    fun gotoIntro()

    fun gotoMainMenu()

    fun gotoGameMenu()

    fun gotoPlay()

    fun saveGame(fileName: String)

    fun loadGame(saveFile: SaveFile)

    fun loadGameFromLastSave()

    fun saveProfile()

    fun loadFromProfile(profile: UserProfile): Boolean

    fun onGameReady(vars: PropertyMap)

    fun exit()

    fun profileNameProperty(): StringProperty

    fun saveScreenshot(): Boolean

    fun restoreDefaultProfileSettings()
}

interface SceneStack {
    fun pushSubScene(subScene: SubScene)

    fun popSubScene()
}