/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.time

import com.almasb.fxgl.app.FXGL
import javafx.util.Duration
import java.time.LocalDateTime

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class OfflineTimer(val name: String) : LocalTimer {

    override fun capture() {
        FXGL.getSystemBundle().put("offline.timer.$name", LocalDateTime.now())
    }

    override fun elapsed(duration: Duration): Boolean {
        val dateTime = FXGL.getSystemBundle().get<LocalDateTime?>("offline.timer.$name")

        if (dateTime == null) {
            capture()
            return true
        }

        return LocalDateTime.now().minusSeconds(duration.toSeconds().toLong()).isAfter(dateTime)
    }
}