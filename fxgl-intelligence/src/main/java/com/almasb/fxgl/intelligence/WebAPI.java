/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.intelligence;

import com.almasb.fxgl.core.util.ResourceExtractor;
import com.almasb.fxgl.logging.Logger;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores constants related to web-api projects.
 * Changes to these values must be synchronized with the web-api project (https://github.com/AlmasB/web-api).
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
public final class WebAPI {

    private static final Logger log = Logger.get(WebAPI.class);

    /**
     * K - URL relative to resources/com.almasb.fxgl.intelligence in jar.
     * V - absolute URL on the file system.
     */
    private static final Map<String, URL> URLS = extractURLs();

    public static final URL TEXT_TO_SPEECH_API = URLS.get("tts/index.html");
    public static final String SPEECH_RECOGNITION_API = "https://almasb.github.io/web-api/speech-recog-v1/";
    public static final String GESTURE_RECOGNITION_API = "https://almasb.github.io/web-api/gesture-recog-v1/";

    public static final int TEXT_TO_SPEECH_PORT = 55550;
    public static final int SPEECH_RECOGNITION_PORT = 55555;
    public static final int GESTURE_RECOGNITION_PORT = 55560;

    private static Map<String, URL> extractURLs() {
        var map = new HashMap<String, URL>();

        List.of(
                "rpc-common.js",
                "tts/index.html",
                "tts/script.js"
        ).forEach(relativeURL -> {
            map.put(relativeURL, extractURL(relativeURL, "intelligence/" + relativeURL));
        });

        return map;
    }

    private static URL extractURL(String relativeURL, String relateFilePath) {
        try {
            var url = WebAPI.class.getResource(relativeURL);

            if (url == null) {
                throw new IllegalArgumentException("URL is null: " + relativeURL);
            }

            return ResourceExtractor.extract(url, relateFilePath);

        } catch (Exception e) {
            log.warning("Failed to get url: " + relativeURL, e);
        }

        // TODO: github URLs, e.g. https://raw.githubusercontent.com/AlmasB/FXGL/dev/fxgl-intelligence/src/main/resources/com/almasb/fxgl/intelligence/rpc-common.js
        throw new IllegalArgumentException("Failed to extract URL: " + relativeURL);
    }
}
