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
    public static final URL SPEECH_RECOGNITION_API = URLS.get("speechrecog/index.html");
    public static final URL GESTURE_RECOGNITION_API = URLS.get("gesturerecog/index.html");
    public static final URL HAND_TRACKING_API = URLS.get("handtracking/index.html");
    public static final URL FACE_DETECTION_API = URLS.get("facedetect/index.html");

    public static final int TEXT_TO_SPEECH_PORT = 55550;
    public static final int SPEECH_RECOGNITION_PORT = 55555;
    public static final int GESTURE_RECOGNITION_PORT = 55560;
    public static final int HAND_TRACKING_PORT = 55565;
    public static final int FACE_DETECTION_PORT = 55570;

    private static Map<String, URL> extractURLs() {
        var map = new HashMap<String, URL>();

        List.of(
                "rpc-common.js",
                "tts/index.html",
                "tts/script.js",
                "gesturerecog/index.html",
                "gesturerecog/script.js",
                "handtracking/index.html",
                "handtracking/script.js",
                "facedetect/index.html",
                "facedetect/script.js",
                "speechrecog/index.html",
                "speechrecog/script.js"
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
