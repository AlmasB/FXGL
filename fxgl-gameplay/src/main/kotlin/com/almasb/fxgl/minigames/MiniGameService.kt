/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.minigames

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.input.KeyTrigger
import com.almasb.fxgl.minigames.circuitbreaker.CircuitBreakerMiniGame
import com.almasb.fxgl.minigames.circuitbreaker.CircuitBreakerResult
import com.almasb.fxgl.minigames.circuitbreaker.CircuitBreakerView
import com.almasb.fxgl.minigames.lockpicking.LockPickResult
import com.almasb.fxgl.minigames.lockpicking.LockPickView
import com.almasb.fxgl.minigames.randomoccurrence.RandomOccurrenceMiniGame
import com.almasb.fxgl.minigames.randomoccurrence.RandomOccurrenceResult
import com.almasb.fxgl.minigames.randomoccurrence.RandomOccurrenceView
import com.almasb.fxgl.minigames.sweetspot.SweetSpotMiniGame
import com.almasb.fxgl.minigames.sweetspot.SweetSpotResult
import com.almasb.fxgl.minigames.sweetspot.SweetSpotView
import com.almasb.fxgl.minigames.triggermash.TriggerMashMiniGame
import com.almasb.fxgl.minigames.triggermash.TriggerMashResult
import com.almasb.fxgl.minigames.triggermash.TriggerMashView
import com.almasb.fxgl.minigames.triggersequence.TriggerSequenceMiniGame
import com.almasb.fxgl.minigames.triggersequence.TriggerSequenceResult
import com.almasb.fxgl.minigames.triggersequence.TriggerSequenceView
import com.almasb.fxgl.scene.SceneService
import com.almasb.fxgl.scene.SubScene
import javafx.scene.input.KeyCode
import javafx.util.Duration
import java.util.function.Consumer

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class MiniGameService : EngineService() {

    private lateinit var sceneService: SceneService

    /**
     * Starts the random occurrence mini game with given [successChance] in range [0..1].
     */
    fun startRandomOccurrence(successChance: Double, callback: Consumer<RandomOccurrenceResult>) {
        val miniGame = RandomOccurrenceMiniGame()
        miniGame.successChance = successChance

        startMiniGame(RandomOccurrenceView(miniGame)) { callback.accept(it) }
    }

    fun startTriggerSequence(keys: List<KeyCode>, winRatio: Double, callback: Consumer<TriggerSequenceResult>) {
        val miniGame = TriggerSequenceMiniGame(winRatio)
        miniGame.triggers += keys.map { KeyTrigger(it) }

        startMiniGame(TriggerSequenceView(miniGame)) { callback.accept(it) }
    }

    fun startSweetSpot(successRange: Int, callback: Consumer<SweetSpotResult>) {
        val miniGame = SweetSpotMiniGame()
        miniGame.randomizeRange(successRange)

        startMiniGame(SweetSpotView(miniGame)) { callback.accept(it) }
    }

    fun startTriggerMash(trigger: KeyTrigger, callback: Consumer<TriggerMashResult>) {
        startTriggerMash(trigger, 1.7, 0.1, callback)
    }

    fun startTriggerMash(trigger: KeyTrigger, boostRate: Double, decayRate: Double, callback: Consumer<TriggerMashResult>) {
        val miniGame = TriggerMashMiniGame(trigger)
        miniGame.boostRate = boostRate
        miniGame.decayRate = decayRate

        startMiniGame(TriggerMashView(trigger, miniGame)) { callback.accept(it) }
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

    fun <S : MiniGameResult, T : MiniGame<S>> startMiniGame(view: MiniGameView<T>, callback: Consumer<S>) {
        startMiniGame(view) { callback.accept(it) }
    }

    fun <S : MiniGameResult, T : MiniGame<S>> startMiniGame(view: MiniGameView<T>, callback: (S) -> Unit) {
        val scene = MiniGameSubScene(sceneService, false, view, callback)

        sceneService.pushSubScene(scene)
    }

    /**
     * Starts the mini game in GameScene rather than a subscene.
     */
    fun <S : MiniGameResult, T : MiniGame<S>> startMiniGameInGame(view: MiniGameView<T>, callback: Consumer<S>) {
        startMiniGameInGame(view) { callback.accept(it) }
    }

    /**
     * Starts the mini game in GameScene rather than a subscene.
     */
    fun <S : MiniGameResult, T : MiniGame<S>> startMiniGameInGame(view: MiniGameView<T>, callback: (S) -> Unit) {
        val scene = MiniGameSubScene(sceneService, true, view, callback)

        sceneService.pushSubScene(scene)
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
        private val sceneService: SceneService,
        override val isAllowConcurrency: Boolean = false,
        val view: MiniGameView<T>, val callback: (S) -> Unit) : SubScene() {

    init {
        view.translateX = sceneService.prefWidth / 2 - view.layoutBounds.width / 2
        view.translateY = sceneService.prefHeight / 2 - view.layoutBounds.height / 2

        contentRoot.children += view

        view.onInitInput(input)
    }

    override fun onUpdate(tpf: Double) {
        view.miniGame.onUpdate(tpf)
        view.onUpdate(tpf)

        if (view.miniGame.isDone) {
            sceneService.popSubScene()

            callback(view.miniGame.result)
        }
    }
}