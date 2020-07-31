/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.app

import com.almasb.fxgl.test.RunWithFX
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertTimeoutPreemptively
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration
import java.util.concurrent.CountDownLatch

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@ExtendWith(RunWithFX::class)
class GameApplicationTest {

    class MockGameApplication : GameApplication() {

        companion object {
            val COUNTDOWN = CountDownLatch(8)
        }

        override fun initSettings(settings: GameSettings) {
            COUNTDOWN.countDown()
        }

        override fun onPreInit() {
            COUNTDOWN.countDown()
        }

        override fun initInput() {
            COUNTDOWN.countDown()
        }

        override fun initGameVars(vars: MutableMap<String, Any>) {
            COUNTDOWN.countDown()
        }

        override fun initGame() {
            COUNTDOWN.countDown()
        }

        override fun initPhysics() {
            COUNTDOWN.countDown()
        }

        override fun initUI() {
            COUNTDOWN.countDown()
        }

        override fun onUpdate(tpf: Double) {
            COUNTDOWN.countDown()
        }
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `High-level FXGL system test`() {
        Thread(Runnable {
            GameApplication.launch(MockGameApplication::class.java, arrayOf())
        }).start()

        assertTimeoutPreemptively(Duration.ofSeconds(10)) {
            MockGameApplication.COUNTDOWN.await()
        }
    }

    @Test
    fun `Default GameApplication methods are no-op`() {
        val app = object : GameApplication() {
            override fun initSettings(settings: GameSettings) { }
        }

        app.initInput()
        app.onPreInit()
        app.initGame()
        app.initPhysics()
        app.initUI()
        app.onUpdate(0.016)
    }
}