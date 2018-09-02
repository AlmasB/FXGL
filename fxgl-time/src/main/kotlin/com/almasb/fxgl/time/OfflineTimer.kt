/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.time

import com.almasb.fxgl.core.serialization.Bundle
import javafx.util.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * Can be used to check if some time has been elapsed since last capture()
 * even when the application is not running.
 * The last time from capture() is saved to a given bundle, which can then be saved to a file.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class OfflineTimer(val name: String, private val bundle: Bundle) : LocalTimer {

    override fun capture() {
        bundle.put("offline.timer.$name", LocalDateTime.now())
    }

    override fun elapsed(duration: Duration): Boolean {
        val dateTime = bundle.get<LocalDateTime?>("offline.timer.$name")

        if (dateTime == null) {
            capture()
            return true
        }

        return LocalDateTime.now().minus(duration.toMillis().toLong(), ChronoUnit.MILLIS).isAfter(dateTime)
    }
}