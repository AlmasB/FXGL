/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene.menu;

import com.almasb.fxgl.saving.SaveFile;
import com.almasb.fxgl.saving.SaveLoadManager;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;

/**
 * Listener for events that occur within menus.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface MenuEventListener {

    void onNewGame();

    void onContinue();

    void onResume();

    void onSave();

    void onLoad(SaveFile saveFile);

    void onDelete(SaveFile saveFile);

    void onLogout();

    void onMultiplayer();

    void onExit();

    void onExitToMainMenu();

    ReadOnlyStringProperty profileNameProperty();

    ReadOnlyBooleanProperty hasSavesProperty();

    void restoreDefaultSettings();

    SaveLoadManager getSaveLoadManager();
}
