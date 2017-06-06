/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.scene.SceneFactory
import com.google.inject.Inject
import com.google.inject.Singleton

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Singleton
internal class MainMenuState
@Inject
private constructor(sceneFactory: SceneFactory) : AppState(sceneFactory.newMainMenu(FXGL.getApp())) {

    override fun onEnter(prevState: State) {
        if (prevState is StartupState
                || prevState is IntroState
                || prevState is GameMenuState) {

            val menuHandler = FXGL.getApp().menuListener as MenuEventHandler

            if (!menuHandler.isProfileSelected())
                menuHandler.showProfileDialog()
        } else {
            throw IllegalArgumentException("Entered MainMenu from illegal state: " + prevState)
        }
    }
}