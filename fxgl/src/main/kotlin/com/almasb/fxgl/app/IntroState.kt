/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.core.event.Subscriber
import com.almasb.fxgl.scene.IntroScene
import com.almasb.fxgl.scene.SceneFactory
import com.almasb.fxgl.scene.intro.IntroFinishedEvent
import com.google.inject.Inject
import com.google.inject.Singleton

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Singleton
internal class IntroState
@Inject
private constructor(private val app: GameApplication,
                    sceneFactory: SceneFactory) : AppState(sceneFactory.newIntro()) {

    private var introFinishedSubscriber: Subscriber? = null
    private var introFinished = false

    override fun onEnter(prevState: State) {
        if (prevState is StartupState) {
            introFinishedSubscriber = FXGL.getEventBus().addEventHandler(IntroFinishedEvent.ANY, {
                introFinished = true
            })

            (scene as IntroScene).startIntro()

        } else {
            throw IllegalArgumentException("Entered IntroState from illegal state: " + prevState)
        }
    }

    override fun onUpdate(tpf: Double) {
        if (introFinished) {
            if (FXGL.getSettings().isMenuEnabled) {
                app.stateMachine.startMainMenu()
            } else {
                FXGL.getApp().startNewGame()
            }
        }
    }

    override fun onExit() {
        introFinishedSubscriber!!.unsubscribe()
        introFinishedSubscriber = null
    }
}