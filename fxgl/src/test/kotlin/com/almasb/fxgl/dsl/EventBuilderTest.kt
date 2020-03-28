/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl

import com.almasb.fxgl.event.EventBus
import com.almasb.fxgl.time.Timer
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.util.Duration
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EventBuilderTest {

    private lateinit var builder: EventBuilder
    private lateinit var bus: EventBus
    private lateinit var timer: Timer

    @BeforeEach
    fun `setUp`() {
        builder = EventBuilder()
        bus = EventBus()
        timer = Timer()
    }

    @Test
    fun `Event fires when condition is met`() {
        var x = 0
        var count = 0

        builder.`when` { x > 1 }
                .thenFire { Event(EventType.ROOT) }
                .buildAndStart(bus, timer)

        bus.addEventHandler(EventType.ROOT, EventHandler {
            count = 1
        })

        timer.update(0.016)
        assertThat(count, `is`(0))

        x = 2

        timer.update(0.016)
        assertThat(count, `is`(1))
    }

    @Test
    fun `Event fires with a given delay`() {
        var x = 0
        var count = 0

        builder.`when` { x > 1 }
                .interval(Duration.seconds(1.0))
                .thenFire { Event(EventType.ROOT) }
                .buildAndStart(bus, timer)

        bus.addEventHandler(EventType.ROOT, EventHandler {
            count = 1
        })

        timer.update(0.016)
        assertThat(count, `is`(0))

        x = 2

        timer.update(0.016)
        assertThat(count, `is`(0))

        timer.update(1.0)
        assertThat(count, `is`(1))
    }

    @Test
    fun `Event does not fire when it reaches the limit`() {
        var x = 0
        var count = 0

        builder.`when` { x > 1 }
                .interval(Duration.seconds(1.0))
                .limit(2)
                .thenFire { Event(EventType.ROOT) }
                .buildAndStart(bus, timer)

        bus.addEventHandler(EventType.ROOT, EventHandler {
            count++
        })

        timer.update(0.016)
        assertThat(count, `is`(0))

        x = 2

        timer.update(1.0)
        assertThat(count, `is`(1))

        timer.update(1.0)
        assertThat(count, `is`(2))

        // we've reached our limit

        timer.update(1.0)
        assertThat(count, `is`(2))
    }
}