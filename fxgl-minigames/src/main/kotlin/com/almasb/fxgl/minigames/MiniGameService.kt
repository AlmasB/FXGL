/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.minigames

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.Inject
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.core.util.Consumer
import com.almasb.fxgl.minigames.circuitbreaker.CircuitBreakerMiniGame
import com.almasb.fxgl.minigames.circuitbreaker.CircuitBreakerResult
import com.almasb.fxgl.minigames.circuitbreaker.CircuitBreakerView
import com.almasb.fxgl.minigames.lockpicking.LockPickResult
import com.almasb.fxgl.minigames.lockpicking.LockPickView
import com.almasb.fxgl.minigames.sweetspot.SweetSpotMiniGame
import com.almasb.fxgl.minigames.sweetspot.SweetSpotResult
import com.almasb.fxgl.minigames.sweetspot.SweetSpotView
import com.almasb.fxgl.scene.SubScene
import com.almasb.fxgl.scene.SubSceneStack
import javafx.event.EventHandler
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.util.Duration

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class MiniGameService : EngineService {

    @Inject("width")
    private var appWidth: Int = 0

    @Inject("height")
    private var appHeight: Int = 0

    @Inject("sceneStack")
    private lateinit var sceneStack: SubSceneStack

    fun startSweetSpot(successRange: Int, callback: Consumer<SweetSpotResult>) {
        val miniGame = SweetSpotMiniGame()
        miniGame.randomizeRange(successRange)

        startMiniGame(SweetSpotView(miniGame)) { callback.accept(it) }
    }

    fun startLockPicking(callback: Consumer<LockPickResult>) {
        startMiniGame(LockPickView()) { callback.accept(it) }
    }

    fun startCircuitBreaker(numHorizontalTiles: Int,
                            numVerticalTiles: Int,
                            playerSize: Double,
                            playerSpeed: Double,
                            miniGameDelay: Duration,
                            callback: Consumer<CircuitBreakerResult>) {

        val miniGame = CircuitBreakerMiniGame(numHorizontalTiles, numVerticalTiles, playerSize, playerSpeed, miniGameDelay)

        startMiniGame(CircuitBreakerView(miniGame)) { callback.accept(it) }
    }

    // TODO: start mini game in the in-game mode, not a different subscene
    fun <S : MiniGameResult, T : MiniGame<S>> startMiniGame(view: MiniGameView<T>, callback: Consumer<S>) {
        startMiniGame(view) { callback.accept(it) }
    }

    fun <S : MiniGameResult, T : MiniGame<S>> startMiniGame(view: MiniGameView<T>, callback: (S) -> Unit) {
        val scene = MiniGameSubScene(sceneStack, appWidth, appHeight, view, callback)

        sceneStack.pushSubScene(scene)
    }

    override fun onMainLoopStarting() {
    }

    override fun onGameReady(vars: PropertyMap) {
    }

    override fun onExit() {
    }

    override fun onUpdate(tpf: Double) {
    }

    override fun write(bundle: Bundle) {
    }

    override fun read(bundle: Bundle) {
    }
}

class MiniGameSubScene<S : MiniGameResult, T : MiniGame<S>>(
        private val sceneStack: SubSceneStack,
        appWidth: Int, appHeight: Int,
        val view: MiniGameView<T>, val callback: (S) -> Unit) : SubScene() {

    init {
        view.translateX = appWidth / 2 - view.layoutBounds.width / 2
        view.translateY = appHeight / 2 - view.layoutBounds.height / 2

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
            sceneStack.popSubScene()

            callback(view.miniGame.result)
        }
    }
}