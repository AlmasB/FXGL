/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.scene

/**
 * Factory for scenes used in FXGL.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
open class SceneFactory {

    open fun newStartup(width: Int, height: Int): StartupScene = FXGLStartupScene(width, height)

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
    open fun newLoadingScene(): LoadingScene = FXGLLoadingScene()

    /**
     * Called to construct main menu.
     *
     * @return main menu
     */
    open fun newMainMenu(): FXGLMenu = FXGLDefaultMenu(MenuType.MAIN_MENU)

    /**
     * Called to construct game menu.
     *
     * @return game menu
     */
    open fun newGameMenu(): FXGLMenu = FXGLDefaultMenu(MenuType.GAME_MENU)
}