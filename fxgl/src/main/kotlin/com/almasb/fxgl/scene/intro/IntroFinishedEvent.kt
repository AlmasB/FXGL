/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene.intro

import javafx.event.Event
import javafx.event.EventType

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class IntroFinishedEvent : Event(ANY) {

    companion object {
        @JvmField val ANY = EventType<IntroFinishedEvent>(Event.ANY, "INTRO_EVENT")
    }

    override fun toString() = "IntroFinishedEvent"
}