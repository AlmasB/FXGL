/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage
import java.util.concurrent.CountDownLatch

/**
 * Used to mock JavaFX application runtime environment.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class MockApplication : Application() {

    companion object {
        val READY = CountDownLatch(1)
        lateinit var stage: Stage
    }

    override fun start(primaryStage: Stage) {
        stage = primaryStage
        stage.scene = Scene(Pane())

        READY.countDown()
    }
}