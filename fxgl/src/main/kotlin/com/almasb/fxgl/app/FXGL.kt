/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.asset.AssetLoader
import com.almasb.fxgl.audio.AudioPlayer
import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.core.reflect.ReflectionUtils
import com.almasb.fxgl.event.EventBus
import com.almasb.fxgl.gameplay.Gameplay
import com.almasb.fxgl.gameplay.notification.NotificationServiceProvider
import com.almasb.fxgl.io.FS
import com.almasb.fxgl.io.serialization.Bundle
import com.almasb.fxgl.net.FXGLNet
import com.almasb.fxgl.scene.menu.MenuSettings
import com.almasb.fxgl.time.LocalTimer
import com.almasb.fxgl.time.OfflineTimer
import com.almasb.fxgl.ui.FXGLDisplay
import com.almasb.fxgl.util.Version
import com.gluonhq.charm.down.Platform
import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.Callable
import java.util.function.Consumer

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

        @JvmStatic fun isDesktop() = Platform.isDesktop()
        @JvmStatic fun isAndroid() = Platform.isAndroid()
        @JvmStatic fun isIOS() = Platform.isIOS()

        /**
         * @return FXGL system settings
         */
        @JvmStatic fun getSettings() = internalApp.settings

        private val _menuSettings = MenuSettings()

        @JvmStatic fun getMenuSettings() = _menuSettings

        private val _gameConfig by lazy {
            val parser = AnnotationParser(internalApp.javaClass)
            parser.parse(SetGameConfig::class.java)
            val config = parser.getClasses(SetGameConfig::class.java)
                    .map { gameConfigClass ->
                        getAssetLoader().loadKV("config.kv").to(gameConfigClass)
                    }
                    .firstOrNull() ?: throw IllegalStateException("No class annotated @SetGameConfig was found")

            config
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

            Version.print()

            internalApp = app

            loadSystemProperties()
            loadUserProperties()

            if (FXGL.isDesktop()) {
                createRequiredDirs()

                if (firstRun)
                    loadDefaultSystemData()
                else
                    loadSystemData()

                runUpdaterAndWait()
            }
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

        private fun loadProperties(props: ResourceBundle) {
            props.keySet().forEach { key ->
                val value = props.getObject(key)
                FXGL.setProperty(key, value)
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

            val systemDir = Paths.get("system/")

            if (!Files.exists(systemDir)) {
                firstRun = true

                Files.createDirectories(systemDir)

                val readmeFile = Paths.get("system/Readme.txt")

                Files.write(readmeFile, "This directory contains FXGL system data files.".lines())
            }
        }

        private fun saveSystemData() {
            log.debug("Saving FXGL system data")

            FS.writeDataTask(internalBundle, "system/fxgl.bundle")
                    .onFailure(Consumer { log.warning("Failed to save: $it") })
                    .execute()
        }

        private fun loadSystemData() {
            log.debug("Loading FXGL system data")

            FS.readDataTask<Bundle>("system/fxgl.bundle")
                    .onSuccess(Consumer {
                        internalBundle = it
                        internalBundle.log()
                    })
                    .onFailure(Consumer {
                        log.warning("Failed to load: $it")
                        loadDefaultSystemData()
                    })
                    .execute()
        }

        private fun loadDefaultSystemData() {
            log.debug("Loading default FXGL system data")

            // populate with default info
            internalBundle = Bundle("FXGL")
        }

        private fun runUpdaterAndWait() {
            Async.startFX { UpdaterTask().run() }.await()
        }

        /**
         * Destructs FXGL.
         */
        @JvmStatic protected fun destroy() {
            if (!configured)
                throw IllegalStateException("FXGL has not been configured")

            saveSystemData()
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
         * @param key property key
         * @return int value
         */
        @JvmStatic fun getInt(key: String) = Integer.parseInt(getProperty(key))

        /**
         * @param key property key
         * @return double value
         */
        @JvmStatic fun getDouble(key: String) = java.lang.Double.parseDouble(getProperty(key))

        /**
         * @param key property key
         * @return boolean value
         */
        @JvmStatic fun getBoolean(key: String) = java.lang.Boolean.parseBoolean(getProperty(key))

        /**
         * @param key property key
         * @return string value
         */
        @JvmStatic fun getString(key: String) = getProperty(key)

        /**
         * @param key property key
         * @return property value
         */
        private fun getProperty(key: String) = System.getProperty("FXGL.$key")
                ?: throw IllegalArgumentException("Key \"$key\" not found!")

        /**
         * Set an int, double, boolean or String property.
         * The value can then be retrieved with FXGL.get* methods.
         *
         * @param key property key
         * @param value property value
         */
        @JvmStatic fun setProperty(key: String, value: Any) {
            System.setProperty("FXGL.$key", value.toString())
        }

        /**
         * @return a string translated to the language used by FXGL game
         */
        @JvmStatic fun getLocalizedString(key: String): String {
            val langName = _menuSettings.getLanguage().resourceBundleName()

            val bundle = getAssetLoader().loadResourceBundle("languages/$langName.properties")

            return bundle.getString(key)
        }

        /**
         * @return binding to a string translated to the language used by FXGL game
         */
        @JvmStatic fun localizedStringProperty(key: String): StringBinding {
            return Bindings.createStringBinding(Callable { getLocalizedString(key) }, _menuSettings.languageProperty())
        }
    }
}