/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.scene.FXGLScene
import com.almasb.fxgl.time.LocalTimer
import com.almasb.fxgl.time.OfflineTimer
import com.almasb.fxgl.time.Timer
import com.gluonhq.charm.down.Platform
import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import java.util.concurrent.Callable

/**
 * Represents the FXGL facade and provides access to engine subsystems.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGL
private constructor() {

    companion object {

        // TODO: effectively private?
        internal lateinit var engine: Engine

        private val _propertyMap = PropertyMap()

        @JvmStatic fun getPropertyMap() = _propertyMap

        @JvmStatic fun getGameController(): GameController = engine

        @JvmStatic fun handleFatalError(error: Throwable) {
            engine.handleFatalError(error)
        }

        internal fun getScene(): FXGLScene {
            return engine.mainWindow.getCurrentScene()
        }

        /* STATIC ACCESSORS */

        @JvmStatic fun getVersion() = engine.version

        // cheap hack for now
        @JvmStatic fun isBrowser() = System.getProperty("fxgl.isBrowser", "false") == "true"

        // javafxports doesn't have "web" option, so will incorrectly default to desktop, hence the extra check
        @JvmStatic fun isDesktop() = !isBrowser() && Platform.isDesktop()
        @JvmStatic fun isMobile() = isAndroid() || isIOS()
        @JvmStatic fun isAndroid() = Platform.isAndroid()
        @JvmStatic fun isIOS() = Platform.isIOS()

        /**
         * @return FXGL system settings
         */
        @JvmStatic fun getSettings(): ReadOnlyGameSettings = engine.settings

        /**
         * @return instance of the running game application
         */
        @JvmStatic fun getApp() = engine.app

        @JvmStatic fun getAppWidth() = engine.settings.width

        @JvmStatic fun getAppHeight() = engine.settings.height

        /**
         * @return instance of the running game application cast to the actual type
         */
        @Suppress("UNCHECKED_CAST")
        @JvmStatic fun <T : GameApplication> getAppCast() = getApp() as T

        @JvmStatic fun getStateMachine() = engine.stateMachine

        /**
         * Note: the system bundle is saved on exit and loaded on init.
         * This bundle is meant to be used by the FXGL system only.
         * If you want to save global (non-gameplay) data use user profiles instead.
         *
         * @return FXGL system data bundle
         */
        @JvmStatic fun getSystemBundle() = engine.bundle

        @JvmStatic fun getUIFactory() = getSettings().uiFactory

        @JvmStatic fun getAssetLoader() = engine.assetLoader

        @JvmStatic fun getEventBus() = engine.eventBus

        @JvmStatic fun getAudioPlayer() = engine.audioPlayer

        @JvmStatic fun getDisplay() = engine.display

        @JvmStatic fun getExecutor() = engine.executor

        @JvmStatic fun getNet() = engine.net

        @JvmStatic fun getGameplay() = engine.gameplay

        /**
         * @return time per frame (in this frame)
         */
        @JvmStatic fun tpf() = engine.loop.tpf

        @JvmStatic fun getGameState() = engine.playState.gameState
        @JvmStatic fun getGameWorld() = engine.playState.gameWorld
        @JvmStatic fun getPhysicsWorld() = engine.playState.physicsWorld
        @JvmStatic fun getGameScene() = engine.playState.gameScene

        /**
         * @return play state input
         */
        @JvmStatic fun getInput(): Input = engine.playState.input

        /**
         * @return play state timer
         */
        @JvmStatic fun getMasterTimer(): Timer = engine.playState.timer

        /**
         * @return new instance on each call
         */
        @JvmStatic fun newLocalTimer() = getMasterTimer().newLocalTimer()

        /**
         * @param name unique name for timer
         * @return new instance on each call
         */
        @JvmStatic fun newOfflineTimer(name: String): LocalTimer = OfflineTimer(name, getSystemBundle())




        // TODO: do these belong here?

        /**
         * @return a string translated to the language used by FXGL game
         */
        @JvmStatic fun getLocalizedString(key: String): String {
            val langName = FXGL.getSettings().language.value.resourceBundleName()

            val bundle = FXGL.getAssetLoader().loadResourceBundle("languages/$langName.properties")

            try {
                return bundle.getString(key)
            } catch (e: Exception) {
                //log.warning("$key is not localized for language ${FXGL.getSettings().language.value}")
                return "MISSING!"
            }
        }

        /**
         * @return binding to a string translated to the language used by FXGL game
         */
        @JvmStatic fun localizedStringProperty(key: String): StringBinding {
            return Bindings.createStringBinding(Callable { getLocalizedString(key) }, FXGL.getSettings().language)
        }
    }
}