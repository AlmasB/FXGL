/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.speechrecog;

import com.almasb.fxgl.core.EngineService;
import com.almasb.fxgl.core.concurrent.Async;
import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.ws.LocalWebSocketServer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.function.Consumer;

import static com.almasb.fxgl.intelligence.WebAPI.SPEECH_RECOGNITION_API;
import static com.almasb.fxgl.intelligence.WebAPI.SPEECH_RECOGNITION_PORT;

public final class SpeechRecognitionService extends EngineService {

    private static final Logger log = Logger.get(SpeechRecognitionService.class);
    private LocalWebSocketServer server = new LocalWebSocketServer("SpeechRecogServer", SPEECH_RECOGNITION_PORT);

    private WebDriver webDriver = null;

    @Override
    public void onInit() {
        server.start();
    }

    /**
     * Starts speech recognition in a background thread.
     * Can be called after stop() to restart speech recognition.
     * If the service has already started, then calls stop() and restarts it.
     */
    public void start() {
        Async.INSTANCE.startAsync(() -> {
            try {
                if (webDriver != null) {
                    stop();
                }

                ChromeOptions options = new ChromeOptions();
                options.addArguments("--headless=new");
                options.addArguments("--use-fake-ui-for-media-stream");

                webDriver = new ChromeDriver(options);
                webDriver.get(SPEECH_RECOGNITION_API);

                // we are ready to use the web api service

            } catch (Exception e) {
                log.warning("Failed to start Chrome web driver. Ensure Chrome is installed in default location");
                log.warning("Error data", e);
            }
        });
    }

    /**
     * Stops speech recognition.
     * No-op if it has not started via start() before.
     */
    public void stop() {
        try {
            if (webDriver != null) {
                webDriver.quit();
                webDriver = null;
            }
        } catch (Exception e) {
            log.warning("Failed to quit web driver", e);
        }
    }

    /**
     * Add a [handler] for incoming speech input.
     */
    public void addInputHandler(Consumer<String> handler) {
        server.addMessageHandler(handler);
    }

    /**
     * Remove an existing input handler.
     */
    public void removeInputHandler(Consumer<String> handler) {
        server.removeMessageHandler(handler);
    }

    @Override
    public void onExit() {
        stop();
        server.stop();
    }
}
