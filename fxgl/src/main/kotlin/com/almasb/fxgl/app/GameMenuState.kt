/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.scene.SceneFactory
import com.google.inject.Inject
import com.google.inject.Singleton
import javafx.scene.input.KeyEvent

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Singleton
internal class GameMenuState
@Inject
private constructor(sceneFactory: SceneFactory) : AppState(sceneFactory.newGameMenu(FXGL.getApp())) {

    init {
        input.addEventHandler(KeyEvent.ANY, FXGL.getApp().menuListener as MenuEventHandler)
    }
}