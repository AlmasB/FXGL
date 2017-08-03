/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.scene.intro.FXGLIntroScene
import com.almasb.fxgl.scene.menu.*

/**
 * Factory for scenes used in FXGL.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
open class SceneFactory {

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
    fun newGameScene(): GameScene = FXGL.getInstance(GameScene::class.java)

    /**
     * Called to construct main menu.
     *
     * @param app game application
     *
     * @return main menu
     */
    open fun newMainMenu(app: GameApplication): FXGLMenu {
        return FXGLDefaultMenu(app, MenuType.MAIN_MENU)
    }

    /**
     * Called to construct game menu.
     *
     * @param app game application
     *
     * @return game menu
     */
    open fun newGameMenu(app: GameApplication): FXGLMenu {
        return FXGLDefaultMenu(app, MenuType.GAME_MENU)
    }
}