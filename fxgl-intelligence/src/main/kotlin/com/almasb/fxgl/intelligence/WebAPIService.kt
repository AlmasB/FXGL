/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.intelligence

import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.net.ws.LocalWebSocketServer
import com.almasb.fxgl.net.ws.RPCService
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.logging.LogType
import org.openqa.selenium.logging.LoggingPreferences
import java.net.URL
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.logging.Level

/**
 * Provides access to JS-driven implementation.
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
abstract class WebAPIService(server: LocalWebSocketServer, private val apiURL: String) : RPCService(server) {

    constructor(server: LocalWebSocketServer, url: URL) : this(server, url.toExternalForm())

    private val log = Logger.get(WebAPIService::class.java)

    private val logExecutor = Executors.newSingleThreadScheduledExecutor()
    private var isLoggingScheduled = false

    private val readyProp = ReadOnlyBooleanWrapper(false)

    var isReady: Boolean
        get() = readyProp.value
        private set(value) { readyProp.value = value }

    /**
     * @return a property that tracks whether this service is ready to be used
     * all changes to the property are notified on the JavaFX thread
     */
    fun readyProperty(): ReadOnlyBooleanProperty {
        return readyProp.readOnlyProperty
    }

    protected fun setReady() {
        Async.startAsyncFX {
            isReady = true
        }
    }

    protected fun setNotReady() {
        Async.startAsyncFX {
            isReady = false
        }
    }

    private var webDriver: WebDriver? = null

    /**
     * Starts this service in a background thread.
     * Can be called after stop() to restart the service.
     * If the service has already started, then calls stop() and restarts it.
     */
    fun start() {
        Async.startAsync {
            try {
                if (webDriver != null) {
                    stop()
                }

                webDriver = loadWebDriverAndPage(apiURL)

                onWebDriverLoaded(webDriver!!)

                if (!isLoggingScheduled) {
                    isLoggingScheduled = true

                    logExecutor.scheduleWithFixedDelay({
                        transferWebDriverLogs()
                    }, 1L, 1L, TimeUnit.SECONDS)
                }

            } catch (e: Exception) {
                log.warning("Failed to start web driver.")
                log.warning("Error data", e)
            }
        }
    }

    private fun loadWebDriverAndPage(url: String): WebDriver {
        val driverSuppliers = listOf(
                { loadChromeDriver() },
                { loadFirefoxDriver() }
        )

        driverSuppliers.forEach { supplier ->
            try {
                log.debug("WebDriver opening: $url")

                val driver = supplier()
                driver.get(url)
                return driver
            } catch (e: Exception) {
                log.warning("Failed to load web driver/page. Ensure Chrome or Firefox is installed in default location", e)
            }
        }

        throw RuntimeException("No valid driver was able to load: $url")
    }

    private fun loadFirefoxDriver(): WebDriver {
        val options = FirefoxOptions()
        options.addArguments("--headless")

        return FirefoxDriver(options)
    }

    private fun loadChromeDriver(): WebDriver {
        val options = ChromeOptions()
        options.addArguments("--headless=new")
        // for modules
        options.addArguments("--allow-file-access-from-files")
        // for webcam, audio input
        options.addArguments("--use-fake-ui-for-media-stream")

        val logPrefs = LoggingPreferences()
        logPrefs.enable(LogType.BROWSER, Level.ALL)

        options.setCapability("goog:loggingPrefs", logPrefs)

        return ChromeDriver(options)
    }

    /**
     * Get all console logs and reroute them to FXGL logs.
     */
    private fun transferWebDriverLogs() {
        try {
            webDriver?.let {
                it.manage()
                    .logs()
                    .get(LogType.BROWSER)
                    .all
                    .forEach {
                        log.debug(it.message)
                    }
            }
        } catch (e: Exception) {
            log.warning("log error", e)
        }
    }

    protected fun executeScript(script: String) {
        try {
            webDriver?.let {
                (it as JavascriptExecutor).executeScript(script)
            }
        } catch (e: Exception) {
            log.warning("Failed to execute script", e)
        }
    }

    /**
     * Stops this service.
     * No-op if it has not started via start() before.
     */
    fun stop() {
        setNotReady()

        try {
            if (webDriver != null) {
                webDriver!!.quit()
                webDriver = null
            }
        } catch (e: Exception) {
            log.warning("Failed to quit web driver", e)
        }
    }

    override fun onExit() {
        logExecutor.shutdownNow()
        stop()
        super.onExit()
    }

    /**
     * Called after the web driver has loaded the page.
     */
    protected open fun onWebDriverLoaded(webDriver: WebDriver) { }
}