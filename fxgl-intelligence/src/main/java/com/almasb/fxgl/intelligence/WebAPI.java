/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.intelligence;

import com.almasb.fxgl.logging.Logger;

/**
 * Stores constants related to web-api projects.
 * Changes to these values must be synchronized with the web-api project (https://github.com/AlmasB/web-api).
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
public final class WebAPI {

    private static final Logger log = Logger.get(WebAPI.class);

    public static final String TEXT_TO_SPEECH_API = getURL("tts/index.html");
    public static final String SPEECH_RECOGNITION_API = "https://almasb.github.io/web-api/speech-recog-v1/";
    public static final String GESTURE_RECOGNITION_API = "https://almasb.github.io/web-api/gesture-recog-v1/";

    public static final int TEXT_TO_SPEECH_PORT = 55550;
    public static final int SPEECH_RECOGNITION_PORT = 55555;
    public static final int GESTURE_RECOGNITION_PORT = 55560;

    static {
        log.debug("TTS API: " + TEXT_TO_SPEECH_API + ":" + TEXT_TO_SPEECH_PORT);
    }

    private static String getURL(String relativeURL) {
        try {
            var url = WebAPI.class.getResource(relativeURL).toExternalForm();

            if (url != null)
                return url;

        } catch (Exception e) {
            log.warning("Failed to get url: " + relativeURL, e);
        }

        return "DUMMY_URL";
    }
}
