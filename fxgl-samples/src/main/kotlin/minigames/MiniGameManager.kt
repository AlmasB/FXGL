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
import com.almasb.fxgl.minigames.MiniGameView
import com.almasb.fxgl.minigames.lockpicking.LockPickResult
import com.almasb.fxgl.minigames.lockpicking.LockPickView
import com.almasb.fxgl.minigames.sweetspot.SweetSpotMiniGame
import com.almasb.fxgl.minigames.sweetspot.SweetSpotResult
import com.almasb.fxgl.minigames.sweetspot.SweetSpotView
import com.almasb.fxgl.scene.SubScene
import javafx.event.EventHandler
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class MiniGameManager {

    fun startSweetSpot(successRange: Int, callback: Consumer<SweetSpotResult>) {
        val miniGame = SweetSpotMiniGame()
        miniGame.randomizeRange(successRange)

        startMiniGame(SweetSpotView(miniGame)) { callback.accept(it) }
    }

    fun startLockPicking(callback: Consumer<LockPickResult>) {
        startMiniGame(LockPickView()) { callback.accept(it) }
    }

    // TODO: start mini game in the in-game mode, not a different subscene
    fun <S : MiniGameResult, T : MiniGame<S>> startMiniGame(view: MiniGameView<T>, callback: Consumer<S>) {
        startMiniGame(view) { callback.accept(it) }
    }

    fun <S : MiniGameResult, T : MiniGame<S>> startMiniGame(view: MiniGameView<T>, callback: (S) -> Unit) {
        val scene = MiniGameSubScene(view, callback)

        FXGL.getGameController().pushSubScene(scene)
    }
}

class MiniGameSubScene<S : MiniGameResult, T : MiniGame<S>>(val view: MiniGameView<T>, val callback: (S) -> Unit) : SubScene() {

    init {
        view.translateX = FXGL.getAppWidth() / 2 - view.layoutBounds.width / 2
        view.translateY = FXGL.getAppHeight() / 2 - view.layoutBounds.height / 2

        contentRoot.children += view

        // TODO: allow only if not repeated, or use the same model as UserAction (begin, run, end)

        input.addEventHandler(KeyEvent.KEY_PRESSED, EventHandler {
            view.onKeyPress(it.code)
        })

        input.addEventHandler(MouseEvent.MOUSE_PRESSED, EventHandler {
            view.onButtonPress(it.button)
        })
    }

    override fun onUpdate(tpf: Double) {
        view.miniGame.onUpdate(tpf)
        view.onUpdate(tpf)

        if (view.miniGame.isDone) {
            FXGL.getGameController().popSubScene()

            callback(view.miniGame.result)
        }
    }
}