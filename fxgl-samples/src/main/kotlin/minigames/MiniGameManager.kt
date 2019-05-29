/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package minigames

import com.almasb.fxgl.core.util.Consumer
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.minigames.MiniGame
import com.almasb.fxgl.minigames.MiniGameResult
import com.almasb.fxgl.scene.SubScene

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class MiniGameManager {

    fun <T : MiniGameResult> startMiniGame(miniGame: MiniGame<T>, callback: Consumer<T>) {
        startMiniGame(miniGame) { callback.accept(it) }
    }

    fun <T : MiniGameResult> startMiniGame(miniGame: MiniGame<T>, callback: (T) -> Unit) {
        val scene = MiniGameSubScene(miniGame, callback)

        FXGL.getGameController().pushSubScene(scene)
    }
}

class MiniGameSubScene<T : MiniGameResult>(val miniGame: MiniGame<T>, val callback: (T) -> Unit) : SubScene() {

    override fun onUpdate(tpf: Double) {
        miniGame.onUpdate(tpf)

        if (miniGame.isDone) {
            FXGL.getGameController().popSubScene()

            callback(miniGame.result)
        }
    }
}