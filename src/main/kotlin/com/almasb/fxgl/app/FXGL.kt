/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.app

import com.almasb.easyio.FS
import com.almasb.easyio.serialization.Bundle
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.logging.MockLoggerFactory
import com.almasb.fxgl.logging.SystemLogger
import com.almasb.fxgl.settings.ReadOnlyGameSettings
import com.almasb.fxgl.time.LocalTimer
import com.almasb.fxgl.time.OfflineTimer
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Provides
import com.google.inject.name.Names
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.util.*
import java.util.function.Consumer

/**
 * Represents the entire FXGL infrastructure.
 * Can be used to pass internal properties (key-value pair) around.
 * Can be used for communication between non-related parts.
 * Not to be abused.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGL private constructor() {

    companion object {
        private lateinit var internalSettings: ReadOnlyGameSettings
        private lateinit var internalApp: GameApplication

        private lateinit var internalBundle: Bundle

        private lateinit var internalLogger: Logger

        /**
         * Temporarily holds k-v pairs from system.properties.
         */
        private val internalProperties = ArrayList<Pair<String, Any> >()

        private var initDone = false

        /**
         * @return FXGL system settings
         */
        @JvmStatic fun getSettings() = internalSettings

        /**
         * @return instance of the running game application
         */
        @JvmStatic fun getApp() = internalApp

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
        @JvmStatic protected fun configure(app: FXGLApplication, stage: Stage) {
            if (initDone)
                throw IllegalStateException("FXGL is already configured")

            initDone = true

            internalApp = app as GameApplication
            internalSettings = app.settings

            createRequiredDirs()
            configureServices(stage)

            // log that we are ready, also force logger service to init
            internalLogger = getLogger("FXGL")
            internalLogger.info("FXGL configuration complete")

            if (firstRun)
                loadDefaultSystemData()
            else
                loadSystemData()
        }

        /**
         * Destructs FXGL.
         */
        @JvmStatic protected fun destroy() {
            if (!initDone)
                throw IllegalStateException("FXGL has not been configured")

            saveSystemData()
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
            internalLogger.debug("Saving FXGL system data")

            FS.writeDataTask(internalBundle, "system/fxgl.bundle")
                    .onFailure(Consumer { internalLogger.warning("Failed to save: $it") })
                    .execute()
        }

        private fun loadSystemData() {
            internalLogger.debug("Loading FXGL system data")

            FS.readDataTask<Bundle>("system/fxgl.bundle")
                    .onSuccess(Consumer {
                        internalBundle = it
                        internalBundle.log()
                    })
                    .onFailure(Consumer {
                        internalLogger.warning("Failed to load: $it")
                        loadDefaultSystemData()
                    })
                    .execute()
        }

        private fun loadDefaultSystemData() {
            internalLogger.debug("Loading default FXGL system data")

            // populate with default info
            internalBundle = Bundle("FXGL")
            //internalBundle.put("version.check", LocalDate.now())
        }

        /**
         * Dependency injector.
         */
        private lateinit var injector: Injector

        /**
         * Obtain an instance of a service.
         * It may be expensive to use this in a loop.
         * Store a reference to the instance instead.
         *
         * @param serviceType service type
         *
         * @return service
         */
        @JvmStatic fun <T> getService(serviceType: ServiceType<T>) = injector.getInstance(serviceType.service())

        /**
         * Obtain an instance of a type.
         * It may be expensive to use this in a loop.
         * Store a reference to the instance instead.
         *
         * @param type type
         *
         * @return instance
         */
        @JvmStatic fun <T> getInstance(type: Class<T>) = injector.getInstance(type)

        private fun configureServices(stage: Stage) {
            injector = Guice.createInjector(object : ServicesModule() {
                private val scene = Scene(Pane())

                override fun configure() {
                    bind(Double::class.java)
                            .annotatedWith(Names.named("appWidth"))
                            .toInstance(internalSettings.getWidth().toDouble())

                    bind(Double::class.java)
                            .annotatedWith(Names.named("appHeight"))
                            .toInstance(internalSettings.getHeight().toDouble())

                    // add internal properties directly to Guice
                    for ((k,v) in internalProperties) {
                        when(v) {
                            is Int -> bind(Int::class.java).annotatedWith(Names.named(k)).toInstance(v)
                            is Double -> bind(Double::class.java).annotatedWith(Names.named(k)).toInstance(v)
                            is Boolean -> bind(Boolean::class.java).annotatedWith(Names.named(k)).toInstance(v)
                            is String -> bind(String::class.java).annotatedWith(Names.named(k)).toInstance(v)
                            else -> SystemLogger.warning("Unknown property type")
                        }
                    }

                    internalProperties.clear()

                    bind(ReadOnlyGameSettings::class.java).toInstance(internalSettings)
                    bind(ApplicationMode::class.java).toInstance(internalSettings.getApplicationMode())

                    val services = ServiceType::class.java
                            .declaredFields
                            .map { it.get(null) as ServiceType<*> }
                            .toList()
                            // also add user specified services
                            .plus(internalSettings.services)

                    // this actually configures services (written in java due to kotlin's confusion over "to")
                    super.configureServices(services)
                }

                @Provides
                internal fun primaryScene(): Scene {
                    return scene
                }

                @Provides
                internal fun primaryStage(): Stage {
                    return stage
                }
            })
        }

        /* CONVENIENCE ACCESSORS */

        private val _loggerFactory by lazy { if (initDone) getService(ServiceType.LOGGER_FACTORY) else MockLoggerFactory }
        @JvmStatic fun getLogger(name: String) = _loggerFactory.newLogger(name)
        @JvmStatic fun getLogger(caller: Class<*>) = _loggerFactory.newLogger(caller)

        private val _assetLoader by lazy { getService(ServiceType.ASSET_LOADER) }
        @JvmStatic fun getAssetLoader() = _assetLoader

        private val _eventBus by lazy { getService(ServiceType.EVENT_BUS) }
        @JvmStatic fun getEventBus() = _eventBus

        private val _input by lazy { getService(ServiceType.INPUT) }
        @JvmStatic fun getInput() = _input

        private val _audioPlayer by lazy { getService(ServiceType.AUDIO_PLAYER) }
        @JvmStatic fun getAudioPlayer() = _audioPlayer

        private val _display by lazy { getService(ServiceType.DISPLAY) }
        @JvmStatic fun getDisplay() = _display

        private val _notification by lazy { getService(ServiceType.NOTIFICATION_SERVICE) }
        @JvmStatic fun getNotificationService() = _notification

        private val _executor by lazy { getService(ServiceType.EXECUTOR) }
        @JvmStatic fun getExecutor() = _executor

        private val _achievement by lazy { getService(ServiceType.ACHIEVEMENT_MANAGER) }
        @JvmStatic fun getAchievementManager() = _achievement

        private val _qte by lazy { getService(ServiceType.QTE) }
        @JvmStatic fun getQTE() = _qte

        private val _net by lazy { getService(ServiceType.NET) }
        @JvmStatic fun getNet() = _net

        private val _pooler by lazy { getService(ServiceType.POOLER) }
        @JvmStatic fun getPooler() = _pooler

        /**
         * @return new instance on each call
         */
        @JvmStatic fun newLocalTimer() = getService(ServiceType.LOCAL_TIMER)

        /**
         * @param name unique name for timer
         * @return new instance on each call
         */
        @JvmStatic fun newOfflineTimer(name: String): LocalTimer = OfflineTimer(name)

        private val _masterTimer by lazy { getService(ServiceType.MASTER_TIMER) }
        @JvmStatic fun getMasterTimer() = _masterTimer

        /**
         * @return new instance on each call
         */
        @JvmStatic fun newProfiler() = getService(ServiceType.PROFILER)

        /**
         * @return default checked exception handler
         */
        @JvmStatic fun getExceptionHandler() = FXGLApplication.getDefaultCheckedExceptionHandler()

        /**
         * Get value of an int property.

         * @param key property key
         * *
         * @return int value
         */
        @JvmStatic fun getInt(key: String) = Integer.parseInt(getProperty(key))

        /**
         * Get value of a double property.

         * @param key property key
         * *
         * @return double value
         */
        @JvmStatic fun getDouble(key: String) = java.lang.Double.parseDouble(getProperty(key))

        /**
         * Get value of a boolean property.

         * @param key property key
         * *
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

            if (!initDone) {

                if (value == "true" || value == "false") {
                    internalProperties.add(Pair(key, java.lang.Boolean.parseBoolean(value as String)))
                } else {
                    try {
                        internalProperties.add(Pair(key, Integer.parseInt(value.toString())))
                    } catch(e: Exception) {
                        try {
                            internalProperties.add(Pair(key, java.lang.Double.parseDouble(value.toString())))
                        } catch(e: Exception) {
                            internalProperties.add(Pair(key, value.toString()))
                        }
                    }
                }
            }
        }
    }
}