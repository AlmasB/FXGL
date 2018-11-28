/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene.menu

import com.almasb.fxgl.saving.SaveFile
import com.almasb.fxgl.saving.SaveLoadManager
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyStringProperty

/**
 * Listener for events that occur within menus.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface MenuEventListener {

    fun getSaveLoadManager(): SaveLoadManager

    fun onNewGame()

    fun onContinue()

    fun onResume()

    fun onSave()

    fun onLoad(saveFile: SaveFile)

    fun onDelete(saveFile: SaveFile)

    fun onLogout()

    fun onMultiplayer()

    fun onExit()

    fun onExitToMainMenu()

    fun profileNameProperty(): ReadOnlyStringProperty

    fun hasSavesProperty(): ReadOnlyBooleanProperty

    fun restoreDefaultSettings()
}