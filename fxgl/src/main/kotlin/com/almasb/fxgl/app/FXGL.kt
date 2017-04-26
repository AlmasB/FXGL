/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

import com.almasb.fxgl.core.logging.FXGLLogger
import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.io.FS
import com.almasb.fxgl.io.serialization.Bundle
import com.almasb.fxgl.service.ServiceType
import com.almasb.fxgl.time.LocalTimer
import com.almasb.fxgl.time.OfflineTimer
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Module
import com.google.inject.name.Named
import com.google.inject.name.Names
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import java.nio.file.Files
import java.nio.file.Paths
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
        private lateinit var internalApp: GameApplication

        private lateinit var internalAllServices: List<ServiceType<*>>

        private lateinit var internalBundle: Bundle

        private lateinit var log: Logger

        /**
         * Temporarily holds k-v pairs from system.properties.
         */
        private val internalProperties = Properties()

        private var configured = false

        /**
         * @return FXGL system settings
         */
        @JvmStatic fun getSettings() = internalApp.settings

        /**
         * @return instance of the running game application
         */
        @JvmStatic fun getApp() = internalApp

        @JvmStatic fun getAppWidth() = internalApp.width

        @JvmStatic fun getAppHeight() = internalApp.height

        @JvmStatic fun getServices() = internalAllServices

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
        @JvmStatic fun configure(appModule: ApplicationModule, vararg modules: Module) {
            if (configured)
                return

            configured = true

            internalApp = appModule.app

            val loggerInit = asyncInitLogger()

            createRequiredDirs()

            val allModules = arrayListOf(*modules)
            allModules.add(buildPropertiesModule())
            allModules.add(appModule)

            injector = Guice.createInjector(allModules)

            internalAllServices = appModule.allServices

            runBlocking { loggerInit.await() }

            // log that we are ready
            log = getLogger("FXGL")
            log.debug("FXGL logger initialized")

            initServices()

            if (firstRun)
                loadDefaultSystemData()
            else
                loadSystemData()
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
            //internalBundle.put("version.check", LocalDate.now())
        }

        private fun asyncInitLogger() = async(CommonPool) {
            val resourceName = when (internalApp.settings.applicationMode) {
                ApplicationMode.DEBUG -> "log4j2-debug.xml"
                ApplicationMode.DEVELOPER -> "log4j2-devel.xml"
                ApplicationMode.RELEASE -> "log4j2-release.xml"
            }

            FXGLLogger.configure(FXGL::class.java.getResource(resourceName).toExternalForm())
        }

        private fun initServices() {
            internalAllServices.forEach {
                getInstance(it.service())
                log.debug("Service <<${it.service().simpleName}>> initialized")
            }
        }

        /**
         * Destructs FXGL.
         */
        @JvmStatic protected fun destroy() {
            if (!configured)
                throw IllegalStateException("FXGL has not been configured")

            saveSystemData()
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

        private fun buildPropertiesModule(): Module {
            return object : AbstractModule() {

                override fun configure() {
                    for ((k, v) in internalProperties.intMap)
                        bind(Int::class.java).annotatedWith(k).toInstance(v)

                    for ((k, v) in internalProperties.doubleMap)
                        bind(Double::class.java).annotatedWith(k).toInstance(v)

                    for ((k, v) in internalProperties.booleanMap)
                        bind(Boolean::class.java).annotatedWith(k).toInstance(v)

                    for ((k, v) in internalProperties.stringMap)
                        bind(String::class.java).annotatedWith(k).toInstance(v)

                    internalProperties.clear()
                }
            }
        }

        /* CONVENIENCE ACCESSORS - SERVICES */

        private val _assetLoader by lazy { getService(ServiceType.ASSET_LOADER) }
        @JvmStatic fun getAssetLoader() = _assetLoader

        private val _eventBus by lazy { getService(ServiceType.EVENT_BUS) }
        @JvmStatic fun getEventBus() = _eventBus

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

        private val _exceptionHandler by lazy { getService(ServiceType.EXCEPTION_HANDLER) }
        @JvmStatic fun getExceptionHandler() = _exceptionHandler

        private val _uiFactory by lazy { getService(ServiceType.UI_FACTORY) }
        @JvmStatic fun getUIFactory() = _uiFactory

        private val _questManager by lazy { getService(ServiceType.QUEST_MANAGER) }
        @JvmStatic fun getQuestManager() = _questManager

        /* OTHER CONVENIENCE ACCESSORS */

        private val _input by lazy { internalApp.input }
        @JvmStatic fun getInput() = _input

        @JvmStatic fun getLogger(name: String) = FXGLLogger.get(name)
        @JvmStatic fun getLogger(caller: Class<*>) = FXGLLogger.get(caller)

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

            if (!configured) {

                if (value == "true" || value == "false") {
                    internalProperties.booleanMap[Names.named(key)] = java.lang.Boolean.parseBoolean(value as String)
                } else {
                    try {
                        internalProperties.intMap[Names.named(key)] = Integer.parseInt(value.toString())
                    } catch(e: Exception) {
                        try {
                            internalProperties.doubleMap[Names.named(key)] = java.lang.Double.parseDouble(value.toString())
                        } catch(e: Exception) {
                            internalProperties.stringMap[Names.named(key)] = value.toString()
                        }
                    }
                }
            }
        }
    }

    private class Properties {
        val intMap = hashMapOf<Named, Int>()
        val doubleMap = hashMapOf<Named, Double>()
        val booleanMap = hashMapOf<Named, Boolean>()
        val stringMap = hashMapOf<Named, String>()

        fun clear() {
            intMap.clear()
            doubleMap.clear()
            booleanMap.clear()
            stringMap.clear()
        }
    }
}