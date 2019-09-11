/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.test.RunWithFX
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertTimeout
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

        assertTimeout(Duration.ofSeconds(10)) {
            MockGameApplication.COUNTDOWN.await()
        }
    }
}