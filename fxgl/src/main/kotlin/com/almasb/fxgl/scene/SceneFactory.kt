/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
    open fun newGameScene(): GameScene = FXGL.getInstance(GameScene::class.java)

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