/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.dev.DebugCameraScene
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.dsl.getGameWorld
import com.almasb.fxgl.dsl.getSettings
import com.almasb.fxgl.dsl.isReleaseMode
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.input.InputModifier
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.logging.Logger
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.input.KeyCode

/**
 * Default FXGL system actions.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object SystemActions {

    private val log = Logger.get(javaClass)

    /**
     * Binds system actions to keys and registers with the [input] service.
     */
    fun bind(input: Input) {
        input.addAction(screenshot(), KeyCode.DIGIT8, InputModifier.CTRL)

        if (FXGL.getSettings().applicationMode != ApplicationMode.RELEASE) {
            if (FXGL.getSettings().isDeveloperMenuEnabled) {
                input.addAction(devOptions(), KeyCode.DIGIT1)
                input.addAction(devConsole(), KeyCode.DIGIT2)
            }

            input.addAction(sysdump(), KeyCode.DIGIT9, InputModifier.CTRL)
            input.addAction(restartGame(), KeyCode.R, InputModifier.CTRL)
            input.addAction(toggleDebugCamera(), KeyCode.DIGIT7, InputModifier.CTRL)
        }
    }

    private fun screenshot() = object : UserAction("Screenshot") {
        override fun onActionBegin() {
            FXGL.getWindowService().saveScreenshot()
            //val ok = FXGL.saveScreenshot()

            //FXGL.getNotificationService().pushNotification(if (ok) FXGL.getLocalizedString("dev.screenshotSaved") else FXGL.getLocalizedString("dev.screenshotFailed"))
        }
    }

    private fun devConsole() = object : UserAction("Dev Console") {

        override fun onActionBegin() {
            if (FXGL.getSettings().applicationMode == ApplicationMode.RELEASE)
                return

            FXGL.getDevService().openConsole()
        }
    }

    private fun devOptions() = object : UserAction("Dev Options") {

        override fun onActionBegin() {
            if (isReleaseMode())
                return

            if (FXGL.getDevService().isDevPaneOpen) {
                FXGL.getDevService().closeDevPane()
            } else {
                FXGL.getDevService().openDevPane()
            }
        }
    }

    private fun sysdump() = object : UserAction("System info dump") {
        override fun onActionBegin() {
            log.info("--- System info dump begin ---")
            log.infof("Entities size: %d", getGameWorld().entities.size)
            log.infof("Components size: %d", getGameWorld().entities.flatMap { it.components }.size)
            //log.infof("Scene graph size: %d", getChildrenSize(FXGL.getWindowService().mainWindow.currentFXGLScene.root))
            log.info("--- System info dump end ---")
        }
    }

    private fun restartGame() = object : UserAction("Restart") {
        override fun onActionBegin() {
            FXGL.getGameController().startNewGame()
        }
    }

    private fun toggleDebugCamera() = object : UserAction("Toggle Debug Camera") {
        private val debugCameraScene by lazy { DebugCameraScene() }

        override fun onActionBegin() {
            getSettings().devEnableDebugCamera.value = !getSettings().devEnableDebugCamera.value

            if (getSettings().devEnableDebugCamera.value) {
                FXGL.getSceneService().pushSubScene(debugCameraScene)
            } else {
                FXGL.getSceneService().popSubScene()
            }
        }
    }

    /**
     * Recursively counts number of children of [node].
     */
    private fun getChildrenSize(node: Node): Int {
        log.debug("Counting children for $node")

        return when (node) {
            is Parent -> node.childrenUnmodifiable.size + node.childrenUnmodifiable.map { getChildrenSize(it) }.sum()
            else      -> 0
        }
    }
}