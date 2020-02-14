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
import com.almasb.fxgl.core.concurrent.IOTask
import com.almasb.fxgl.core.reflect.ReflectionUtils.*
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.sslogger.Logger
import javafx.util.Duration

/**
 * FXGL engine is mostly a collection of services, all controlled
 * via callbacks driven by the main loop.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class Engine(val settings: ReadOnlyGameSettings)  {

    private val log = Logger.get(javaClass)

    val loop = LoopRunner { loop(it) }

    val tpf: Double
        get() = loop.tpf

    internal val services = arrayListOf<EngineService>()
    private val servicesCache = hashMapOf<Class<out EngineService>, EngineService>()

    // TODO: make this a local var?
    internal val environmentVars = HashMap<String, Any>()

    init {
        logVersion()
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

    inline fun <reified T : EngineService> getService(serviceClass: Class<T>): T {
        if (servicesCache.containsKey(serviceClass))
            return servicesCache[serviceClass] as T

        return (services.find { it is T  }?.also { servicesCache[serviceClass] = it }
                ?: throw IllegalArgumentException("Engine does not have service: $serviceClass")) as T
    }

    /**
     * This method starts initialization of services on a background thread and returns immediately.
     * The loop is started on the JavaFX thread, as soon as the services are finished initializing.
     */
    fun initServicesAndStartLoop() {
        val fxTask = IOTask.ofVoid {
            initEnvironmentVars()
            initServices()
        }
        .onSuccess {
            services.forEach { it.onMainLoopStarting() }

            loop.start()
        }
        .onFailure {
            throw it
        }.toJavaFXTask()

        Async.execute(fxTask)
    }

    private fun initServices() {
        val start = System.nanoTime()

        settings.engineServices.forEach {
            services += (newInstance(it))
        }

        services.forEach {
            injectDependenciesIntoService(it)
        }

        services.forEach {
            it.onInit()
        }

        log.infof("FXGL initialization took: %.3f sec", (System.nanoTime() - start) / 1000000000.0)

        Async.schedule({ logEnvironmentVarsAndServices() }, Duration.seconds(3.0))
    }

    private fun initEnvironmentVars() {
        log.debug("Initializing environment variables")

        settings.javaClass.declaredMethods.filter { it.name.startsWith("is") || it.name.startsWith("get") || it.name.endsWith("Property") }.forEach {
            environmentVars[it.name.removePrefix("get").decapitalize()] = it.invoke(settings)
        }
    }

    private fun injectDependenciesIntoService(service: EngineService) {
        findFieldsByAnnotation(service, Inject::class.java).forEach { field ->
            val injectKey = field.getDeclaredAnnotation(Inject::class.java).value

            if (injectKey !in environmentVars) {
                throw IllegalArgumentException("Cannot inject @Inject($injectKey). No value present for $injectKey")
            }

            inject(field, service, environmentVars[injectKey])
        }

        findFieldsByTypeRecursive(service, EngineService::class.java).forEach { field ->

            val provider = services.find { field.type.isAssignableFrom(it.javaClass) }
                    ?: throw IllegalStateException("No provider found for ${field.type}")

            inject(field, service, provider)
        }
    }

    private fun logEnvironmentVarsAndServices() {
        log.debug("Logging environment variables")

        environmentVars.forEach { (key, value) ->
            log.debug("$key: $value")
        }

        log.debug("Logging services")

        services.forEach {
            log.debug("${it.javaClass}")
        }
    }

    private fun loop(tpf: Double) {
        services.forEach { it.onUpdate(tpf) }
    }

    fun onGameReady(vars: PropertyMap) {
        services.forEach { it.onGameReady(vars) }
    }

    fun stopLoopAndExitServices() {
        loop.stop()

        services.forEach { it.onExit() }
    }

    fun pauseLoop() {
        loop.pause()
    }

    fun resumeLoop() {
        loop.resume()
    }

    fun write(bundle: Bundle) {
        settings.write(bundle)
        services.forEach { it.write(bundle) }
    }

    fun read(bundle: Bundle) {
        settings.read(bundle)
        services.forEach { it.read(bundle) }
    }
}