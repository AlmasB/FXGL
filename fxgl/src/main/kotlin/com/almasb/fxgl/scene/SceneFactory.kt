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
        when (app.settings.menuStyle) {
            MenuStyle.GTA5 -> return GTAVMenu(app, MenuType.MAIN_MENU)
            MenuStyle.CCTR -> return CCTRMenu(app, MenuType.MAIN_MENU)
            MenuStyle.WARCRAFT3 -> return Warcraft3Menu(app, MenuType.MAIN_MENU)
            else -> return FXGLDefaultMenu(app, MenuType.MAIN_MENU)
        }
    }

    /**
     * Called to construct game menu.
     *
     * @param app game application
     *
     * @return game menu
     */
    open fun newGameMenu(app: GameApplication): FXGLMenu {
        when (app.settings.menuStyle) {
            MenuStyle.GTA5 -> return GTAVMenu(app, MenuType.GAME_MENU)
            MenuStyle.CCTR -> return CCTRMenu(app, MenuType.GAME_MENU)
            MenuStyle.WARCRAFT3 -> return Warcraft3Menu(app, MenuType.GAME_MENU)
            else -> return FXGLDefaultMenu(app, MenuType.GAME_MENU)
        }
    }
}