/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.intelligence.gesturerecog

/**
 * The ordinal of each item matches the format defined at:
 * https://developers.google.com/mediapipe/solutions/vision/hand_landmarker
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
enum class HandLandmark {
    WRIST,

    THUMB_CMC,
    THUMB_MCP,
    THUMB_IP,
    THUMB_TIP,

    INDEX_FINGER_MCP,
    INDEX_FINGER_PIP,
    INDEX_FINGER_DIP,
    INDEX_FINGER_TIP,

    MIDDLE_FINGER_MCP,
    MIDDLE_FINGER_PIP,
    MIDDLE_FINGER_DIP,
    MIDDLE_FINGER_TIP,

    RING_FINGER_MCP,
    RING_FINGER_PIP,
    RING_FINGER_DIP,
    RING_FINGER_TIP,

    PINKY_MCP,
    PINKY_PIP,
    PINKY_DIP,
    PINKY_TIP
}