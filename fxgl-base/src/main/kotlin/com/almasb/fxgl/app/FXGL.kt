/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.app.SystemPropertyKey.FXGL_VERSION
import com.almasb.fxgl.asset.AssetLoader
import com.almasb.fxgl.audio.AudioPlayer
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.core.concurrent.IOTask
import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.event.EventBus
import com.almasb.fxgl.gameplay.Gameplay
import com.almasb.fxgl.gameplay.notification.NotificationServiceProvider
import com.almasb.fxgl.io.FS
import com.almasb.fxgl.io.serialization.Bundle
import com.almasb.fxgl.net.FXGLNet
import com.almasb.fxgl.saving.LoadEvent
import com.almasb.fxgl.saving.SaveEvent
import com.almasb.fxgl.scene.menu.MenuSettings
import com.almasb.fxgl.time.LocalTimer
import com.almasb.fxgl.time.OfflineTimer
import com.almasb.fxgl.ui.FXGLDisplay
import com.gluonhq.charm.down.Platform
import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.event.EventHandler
import java.io.IOException
import java.util.*
import java.util.concurrent.Callable

/**
 * Represents the entire FXGL infrastructure.
 * Can be used to pass internal properties (key-value pair) around.
 * The properties are NOT to be used in gameplay.
 * Can be used for communication between non-related parts.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGL private constructor() {

    companion object {
        private lateinit var internalApp: GameApplication

        private lateinit var internalBundle: Bundle

        private val log = Logger.get("FXGL")

        private var configured = false

        private val props = PropertyMap()

        // cheap hack for now
        @JvmStatic fun isBrowser() = System.getProperty("fxgl.isBrowser", "false") == "true"

        // javafxports doesn't have "web" option, so will incorrectly default to desktop, hence the extra check
        @JvmStatic fun isDesktop() = !isBrowser() && Platform.isDesktop()
        @JvmStatic fun isMobile() = isAndroid() || isIOS()
        @JvmStatic fun isAndroid() = Platform.isAndroid()
        @JvmStatic fun isIOS() = Platform.isIOS()

        @JvmStatic fun getProperties() = props

        /**
         * @return FXGL system settings
         */
        @JvmStatic fun getSettings() = internalApp.settings

        private val _menuSettings = MenuSettings()

        @JvmStatic fun getMenuSettings() = _menuSettings

        private val _gameConfig by lazy {
            getSettings().configClass
                    .map { getAssetLoader().loadKV("config.kv").to(it) }
                    .orElseThrow { IllegalStateException("No config class. You can set it via settings.setConfigClass()") }
        }

        @Suppress("UNCHECKED_CAST")
        @JvmStatic fun <T> getGameConfig() = _gameConfig as T

        /**
         * @return instance of the running game application
         */
        @JvmStatic fun getApp() = internalApp

        @JvmStatic fun getAppWidth() = internalApp.width

        @JvmStatic fun getAppHeight() = internalApp.height

        /**
         * @return instance of the running game application cast to the actual type
         */
        @Suppress("UNCHECKED_CAST")
        @JvmStatic fun <T : GameApplication> getAppCast() = internalApp as T

        /**
         * Note: the system bundle is saved on exit and loaded on init.
         * This bundle is meant to be used by the FXGL system only.
         * If you want to save global (non-gameplay) data use user profiles instead.
         *
         * @return FXGL system data bundle
         */
        @JvmStatic fun getSystemBundle() = internalBundle

        /**
         * Constructs FXGL.
         */
        @JvmStatic fun configure(app: GameApplication) {
            if (configured)
                return

            configured = true

            internalApp = app

            loadSystemProperties()
            loadUserProperties()

            logVersion()

            IOTask.setDefaultExecutor(getExecutor())
            IOTask.setDefaultFailAction(getExceptionHandler())

            createRequiredDirs()

            if (firstRun)
                loadDefaultSystemData()
            else
                loadSystemData()

            if (isDesktop()) {
                runUpdaterAsync()
            }

            // TODO: redesign where save / load listeners should be
            _eventBus.addEventHandler(SaveEvent.ANY, EventHandler { _menuSettings.save(it.profile) })
            _eventBus.addEventHandler(LoadEvent.ANY, EventHandler { _menuSettings.load(it.profile) })
        }

        private fun logVersion() {
            val platform = "${Platform.getCurrent()}" + if (isBrowser()) " BROWSER" else ""

            log.info("FXGL-${props.getString(FXGL_VERSION)} on $platform")
            log.info("Source code and latest versions at: https://github.com/AlmasB/FXGL")
            log.info("             Join the FXGL chat at: https://gitter.im/AlmasB/FXGL")
        }

        private fun loadSystemProperties() {
            loadProperties(ResourceBundle.getBundle("com.almasb.fxgl.app.system"))
        }

        /**
         * Load user defined properties to override FXGL system properties.
         */
        private fun loadUserProperties() {
            // services are not ready yet, so load manually
            try {
                FXGL::class.java.getResource("/assets/properties/system.properties").openStream().use {
                    loadProperties(PropertyResourceBundle(it))
                }
            } catch (npe: NullPointerException) {
                // user properties file not found
            } catch (e: IOException) {
                log.warning("Loading user properties failed: $e")
            }
        }

        private fun loadProperties(bundle: ResourceBundle) {
            bundle.keySet().forEach { key ->
                val value = bundle.getObject(key)
                props.setValueFromString(key, value.toString())
            }
        }

        private var firstRun = false

        /**
         * @return true iff FXGL is running for the first time
         * @implNote we actually check if "system/" exists in running dir, so if it was
         *            deleted, then this method also returns true
         */
        @JvmStatic fun isFirstRun() = firstRun

        private fun createRequiredDirs() {
            if (FS.exists("system/"))
                return

            firstRun = true

            FS.createDirectoryTask("system/")
                    .then { FS.writeDataTask(listOf("This directory contains FXGL system data files."), "system/Readme.txt") }
                    .onFailure { e ->
                        log.warning("Failed to create system dir: $e")
                        Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e)
                    }
                    .run()
        }

        private fun saveSystemData() {
            log.debug("Saving FXGL system data")

            FS.writeDataTask(internalBundle, "system/fxgl.bundle")
                    .onFailure { log.warning("Failed to save: $it") }
                    .run()
        }

        private fun loadSystemData() {
            log.debug("Loading FXGL system data")

            FS.readDataTask<Bundle>("system/fxgl.bundle")
                    .onSuccess {
                        internalBundle = it
                        internalBundle.log()
                    }
                    .onFailure {
                        log.warning("Failed to load: $it")
                        loadDefaultSystemData()
                    }
                    .run()
        }

        private fun loadDefaultSystemData() {
            log.debug("Loading default FXGL system data")

            // populate with default info
            internalBundle = Bundle("FXGL")
        }

        private fun runUpdaterAsync() {
            Async.start { UpdaterTask().run() }
        }

        /**
         * Destructs FXGL.
         */
        @JvmStatic protected fun destroy() {
            if (!configured)
                throw IllegalStateException("FXGL has not been configured")

            if (isDesktop()) {
                saveSystemData()
            }
        }

        @JvmStatic fun getExceptionHandler() = getSettings().exceptionHandler
        @JvmStatic fun getUIFactory() = getSettings().uiFactory

        private val _notificationService by lazy { NotificationServiceProvider() }
        @JvmStatic fun getNotificationService() = _notificationService

        private val _assetLoader by lazy { AssetLoader() }
        @JvmStatic fun getAssetLoader() = _assetLoader

        private val _eventBus by lazy { EventBus() }
        @JvmStatic fun getEventBus() = _eventBus

        private val _audioPlayer by lazy { AudioPlayer() }
        @JvmStatic fun getAudioPlayer() = _audioPlayer

        private val _display by lazy { FXGLDisplay() }
        @JvmStatic fun getDisplay() = _display

        private val _executor by lazy { FXGLExecutor() }
        @JvmStatic fun getExecutor() = _executor

        private val _net by lazy { FXGLNet() }
        @JvmStatic fun getNet() = _net

        private val _gameplay by lazy { Gameplay() }
        @JvmStatic fun getGameplay() = _gameplay

        private val _input by lazy { internalApp.input }
        @JvmStatic fun getInput() = _input

        /**
         * @return new instance on each call
         */
        @JvmStatic fun newLocalTimer() = internalApp.stateMachine.playState.timer.newLocalTimer()

        /**
         * @param name unique name for timer
         * @return new instance on each call
         */
        @JvmStatic fun newOfflineTimer(name: String): LocalTimer = OfflineTimer(name)

        private val _masterTimer by lazy { internalApp.masterTimer }
        @JvmStatic fun getMasterTimer() = _masterTimer

        /**
         * @return a string translated to the language used by FXGL game
         */
        @JvmStatic fun getLocalizedString(key: String): String {
            val langName = _menuSettings.getLanguage().resourceBundleName()

            val bundle = getAssetLoader().loadResourceBundle("languages/$langName.properties")

            try {
                return bundle.getString(key)
            } catch (e: Exception) {
                log.warning("$key is not localized for language ${_menuSettings.getLanguage()}")
                return "MISSING!"
            }
        }

        /**
         * @return binding to a string translated to the language used by FXGL game
         */
        @JvmStatic fun localizedStringProperty(key: String): StringBinding {
            return Bindings.createStringBinding(Callable { getLocalizedString(key) }, _menuSettings.languageProperty())
        }
    }
}