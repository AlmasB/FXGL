/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net.ws

/**
 * This captures the WebAPI from
 * https://developer.mozilla.org/en-US/docs/Web/API/MediaDevices/getUserMedia
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
open class MediaDeviceInfo
internal constructor(
    val kind: String,
    val label: String,
    val id: String
)

class AudioInputDeviceInfo(label: String, id: String)
    : MediaDeviceInfo("audioinput", label, id) {

    override fun toString(): String {
        if (label.isNotEmpty())
            return "(audio)$label"

        return "(audio)$id"
    }
}

class VideoInputDeviceInfo(label: String, id: String)
    : MediaDeviceInfo("videoinput", label, id) {

    override fun toString(): String {
        if (label.isNotEmpty())
            return "(video)$label"

        return "(video)$id"
    }
}