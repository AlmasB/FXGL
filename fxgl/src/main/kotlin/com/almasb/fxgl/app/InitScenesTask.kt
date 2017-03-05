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

package com.almasb.fxgl.app

import com.google.inject.Inject
import javafx.scene.input.KeyEvent

/**
 * A task for initializing all scenes used in FXGL.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class InitScenesTask
@Inject constructor(private val app: GameApplication) : Runnable {

    private val log = FXGL.getLogger(javaClass)

    override fun run() {
        val sceneFactory = app.initSceneFactory()

        val gameScene = sceneFactory.newGameScene()
        val loadingScene = sceneFactory.newLoadingScene()

        if (app.settings.isIntroEnabled) {
            val introScene = sceneFactory.newIntro()

            app.display.registerScene(introScene)

            log.debug("Injecting introScene")
            app.introScene = introScene
        }

        if (app.settings.isMenuEnabled) {
            val menuHandler = app.menuListener

            val mainMenuScene = sceneFactory.newMainMenu(app)
            val gameMenuScene = sceneFactory.newGameMenu(app)

            // add handler to respond to menu key (default ESCAPE)
            gameScene.addEventHandler(KeyEvent.ANY, menuHandler as MenuEventHandler)
            gameMenuScene.addEventHandler(KeyEvent.ANY, menuHandler)

            app.display.registerScene(mainMenuScene)
            app.display.registerScene(gameMenuScene)

            log.debug("Injecting mainMenuScene & gameMenuScene")
            app.mainMenuScene = mainMenuScene
            app.gameMenuScene = gameMenuScene
        }

        app.display.registerScene(loadingScene)
        app.display.registerScene(gameScene)

        log.debug("Injecting loadingScene & gameScene")
        app.loadingScene = loadingScene
        app.gameScene = gameScene
    }
}