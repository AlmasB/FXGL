package com.almasb.fxgl.manual

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.GameSettings

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class GameApp : GameApplication() {
    override fun initSettings(settings: GameSettings?) {
    }
}

fun main(args: Array<String>) {
    GameApplication.launch(GameApp::class.java, args)
}