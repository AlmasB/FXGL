/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.scene.intro.FXGLIntroScene
import com.almasb.fxgl.scene.menu.FXGLDefaultMenu
import com.almasb.fxgl.scene.menu.MenuType

/**
 * Factory for scenes used in FXGL.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
open class SceneFactory {

    /**
     * Called to construct startup scene.
     *
     * @return startup scene
     */
    open fun newStartup(): FXGLScene = StartupScene()

    /**
     * Called to construct intro scene.
     *
     * @return intro scene
     */
    open fun newIntro(): IntroScene = FXGLIntroScene()

    /**
     * Called to construct loading scene.
     *
     * @return loading scene
     */
    open fun newLoadingScene(): LoadingScene = LoadingScene()

    /**
     * Called to construct game scene.
     *
     * @return game scene
     */
    fun newGameScene(width: Int, height: Int): GameScene = GameScene(width, height)

    /**
     * Called to construct main menu.
     *
     * @param app game application
     * @return main menu
     */
    open fun newMainMenu(app: GameApplication): FXGLMenu = FXGLDefaultMenu(app, MenuType.MAIN_MENU)

    /**
     * Called to construct game menu.
     *
     * @param app game application
     * @return game menu
     */
    open fun newGameMenu(app: GameApplication): FXGLMenu = FXGLDefaultMenu(app, MenuType.GAME_MENU)
}