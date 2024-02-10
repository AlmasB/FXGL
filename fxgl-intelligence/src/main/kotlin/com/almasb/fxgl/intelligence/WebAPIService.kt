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
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

/**
 * Provides access to JS-driven implementation.
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
abstract class WebAPIService(server: LocalWebSocketServer, private val apiURL: String) : RPCService(server) {

    private val log = Logger.get(WebAPIService::class.java)

    private val readyProp = ReadOnlyBooleanWrapper(false)

    var isReady: Boolean
        get() = readyProp.value
        protected set(value) { readyProp.value = value }

    fun readyProperty(): ReadOnlyBooleanProperty {
        return readyProp.readOnlyProperty
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

                val options = ChromeOptions()
                options.addArguments("--headless=new")
                options.addArguments("--use-fake-ui-for-media-stream")

                webDriver = ChromeDriver(options)
                webDriver!!.get(apiURL)

                onWebDriverLoaded(webDriver!!)
            } catch (e: Exception) {
                log.warning("Failed to start Chrome web driver. Ensure Chrome is installed in default location")
                log.warning("Error data", e)
            }
        }
    }

    /**
     * Stops this service.
     * No-op if it has not started via start() before.
     */
    fun stop() {
        try {
            if (webDriver != null) {
                webDriver!!.quit()
                webDriver = null
            }
        } catch (e: Exception) {
            log.warning("Failed to quit web driver", e)
        }
    }

    /**
     * Called after the web driver has loaded the page.
     */
    protected open fun onWebDriverLoaded(webDriver: WebDriver) { }
}