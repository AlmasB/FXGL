/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.Inject
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.core.reflect.ReflectionUtils.*
import com.almasb.fxgl.ui.ErrorDialog
import com.almasb.sslogger.Logger
import javafx.application.Platform

/**
 * FXGL engine is mostly a collection of services, all controlled
 * via callbacks driven by the main loop.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class Engine(val settings: ReadOnlyGameSettings)  {

    private val log = Logger.get(javaClass)

    private val loop = LoopRunner { loop(it) }

    val tpf: Double
        get() = loop.tpf

    private val services = arrayListOf<EngineService>()
    private val servicesCache = hashMapOf<Class<out EngineService>, EngineService>()

    private val environmentVars = hashMapOf<String, Any>()

    init {
        log.debug("Initializing FXGL")

        initFatalExceptionHandler()

        logVersion()

        initEnvironmentVars()

        logEnvironmentVars()
    }

    private fun initFatalExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler { _, error -> handleFatalError(error) }
    }

    private fun logVersion() {
        val jVersion = System.getProperty("java.version", "?")
        val fxVersion = System.getProperty("javafx.version", "?")

        val version = settings.runtimeInfo.version
        val build = settings.runtimeInfo.build

        log.info("FXGL-$version ($build) on ${settings.platform} (J:$jVersion FX:$fxVersion)")
        log.info("Source code and latest versions at: https://github.com/AlmasB/FXGL")
        log.info("             Join the FXGL chat at: https://gitter.im/AlmasB/FXGL")
    }

    private fun initEnvironmentVars() {
        log.debug("Initializing environment variables")

        settings.javaClass.declaredMethods.filter { it.name.startsWith("is") || it.name.startsWith("get") || it.name.endsWith("Property") }.forEach {
            environmentVars[it.name.removePrefix("get").decapitalize()] = it.invoke(settings)
        }
    }

    private fun logEnvironmentVars() {
        log.debug("Logging environment variables")

        environmentVars.forEach { (key, value) ->
            log.debug("$key: $value")
        }
    }

    fun addService(engineService: EngineService) {
        log.debug("Adding new engine service: ${engineService.javaClass}")

        services += engineService
    }

    inline fun <reified T : EngineService> getService(serviceClass: Class<T>): T {
        if (servicesCache.containsKey(serviceClass))
            return servicesCache[serviceClass] as T

        return (services.find { it is T  }?.also { servicesCache[serviceClass] = it }
                ?: throw IllegalArgumentException("Engine does not have service: $serviceClass")) as T
    }

    fun startLoop() {
        val start = System.nanoTime()

        // give control back to FX thread while we do heavy init stuff
        Async.startAsync {
            initServices()

            // finish init on FX thread
            Async.startAsyncFX {

                services.forEach { it.onMainLoopStarting() }

                log.infof("FXGL initialization took: %.3f sec", (System.nanoTime() - start) / 1000000000.0)

                loop.start()
            }
        }
    }

    private fun initServices() {
        injectDependenciesIntoServices()
        injectServicesIntoServices()

        services.forEach { it.onInit() }
    }

    private fun injectDependenciesIntoServices() {
        services.forEach { service ->
            findFieldsByAnnotation(service, Inject::class.java).forEach { field ->
                val injectKey = field.getDeclaredAnnotation(Inject::class.java).value

                if (injectKey !in environmentVars) {
                    throw IllegalArgumentException("Cannot inject @Inject($injectKey). No value present for $injectKey")
                }

                inject(field, service, environmentVars[injectKey])
            }
        }
    }

    private fun injectServicesIntoServices() {
        services.forEach { service ->
            findFieldsByTypeRecursive(service, EngineService::class.java).forEach { field ->

                val provider = services.find { field.type.isAssignableFrom(it.javaClass) }
                        ?: throw IllegalStateException("No provider found for ${field.type}")

                inject(field, service, provider)
            }
        }
    }

    private fun loop(tpf: Double) {
        services.forEach { it.onUpdate(tpf) }
    }

    fun onGameReady(vars: PropertyMap) {
        services.forEach { it.onGameReady(vars) }
    }

    private var handledOnce = false

    private fun handleFatalError(e: Throwable) {
        if (handledOnce) {
            // just ignore to avoid spamming dialogs
            return
        }

        handledOnce = true

        val error = if (e is Exception) e else RuntimeException(e)

        if (Logger.isConfigured()) {
            log.fatal("Uncaught Exception:", error)
            log.fatal("Application will now exit")
        } else {
            println("Uncaught Exception:")
            error.printStackTrace()
            println("Application will now exit")
        }

        // stop main loop from running as we cannot continue
        loop.stop()

        // assume we are running on JavaFX Application thread
        // block with error dialog so that user can read the error
        ErrorDialog(error).showAndWait()

        if (loop.isStarted) {
            // exit normally
            exit()
        } else {
            if (Logger.isConfigured()) {
                Logger.close()
            }

            // we failed during launch, so abnormal exit
            System.exit(-1)
        }
    }

    fun exit() {
        log.debug("Exiting FXGL")

        loop.stop()

        services.forEach { it.onExit() }

        log.debug("Shutting down background threads")
        Async.shutdownNow()

        log.debug("Closing logger and exiting JavaFX")
        Logger.close()
        Platform.exit()
    }

    //    internal val assetLoader by lazy { AssetLoader() }




    // TODO: refactor below
//    internal val saveLoadManager by lazy { SaveLoadService(fs) }
//    private fun initSaveLoadHandler() {
//        saveLoadManager.addHandler(object : SaveLoadHandler {
//            override fun onSave(data: DataFile) {
//                // TODO:
//
//                // settings.write()
//                // services.write()
//            }
//
//            override fun onLoad(data: DataFile) {
//                // settings.read()
//                // services.read()
//            }
//        })
//    }
}