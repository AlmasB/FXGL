/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.event

import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EventBusTest {

    private lateinit var eventBus: EventBus

    @BeforeEach
    fun setUp() {
        eventBus = EventBus()
    }

    @Test
    fun `Fire event`() {
        var count = 0

        val handler = EventHandler<Event> { count++ }

        eventBus.addEventHandler(EventType.ROOT, handler)

        assertAll(
                Executable {
                    eventBus.fireEvent(Event(EventType.ROOT))

                    assertThat(count, `is`(1))
                },

                Executable {
                    eventBus.removeEventHandler(EventType.ROOT, handler)

                    eventBus.fireEvent(Event(EventType.ROOT))

                    assertThat(count, `is`(1))
                }
        )
    }

    @Test
    fun `Test handler scan syntax`() {
        eventBus.scanForHandlers(validObject)

        var count = 0

        try {
            eventBus.scanForHandlers(invalidObject0)
        } catch (e: IllegalArgumentException) {
            assertThat(e.message, containsString("is not of type EventType"))
            count++
        }

        assertThat(count, `is`(1))

        try {
            eventBus.scanForHandlers(invalidObject1)
        } catch (e: IllegalArgumentException) {
            assertThat(e.message, containsString("public static field not found"))
            count++
        }

        assertThat(count, `is`(2))

        try {
            eventBus.scanForHandlers(invalidObject2)
        } catch (e: IllegalArgumentException) {
            assertThat(e.message, containsString("public static field not found"))
            count++
        }

        assertThat(count, `is`(3))

        try {
            eventBus.scanForHandlers(invalidObject3)
        } catch (e: IllegalArgumentException) {
            assertThat(e.message, containsString("must have a single parameter"))
            count++
        }

        assertThat(count, `is`(4))

        try {
            eventBus.scanForHandlers(invalidObject4)
        } catch (e: IllegalAccessException) {
            assertThat(e.message, containsString("access"))
            count++
        }

        assertThat(count, `is`(5))
    }

    object validObject {
        @Handles(eventType = "ANY")
        fun handles(event: TestEvent) {
            // cures unused variable warnings
            event.eventType
        }
    }

    object invalidObject0 {
        @Handles(eventType = "FAIL0")
        fun handles(event: TestEvent) {
            event.eventType
        }
    }

    object invalidObject1 {
        @Handles(eventType = "FAIL1")
        fun handles(event: TestEvent) {
            event.eventType
        }
    }

    object invalidObject2 {
        @Handles(eventType = "FAIL2")
        fun handles(event: TestEvent) {
            event.eventType
        }
    }

    object invalidObject3 {
        @Handles(eventType = "FAIL3")
        fun handles() {

        }
    }

    object invalidObject4 {
        @Handles(eventType = "HIDDEN")
        fun handles(event: TestEvent) {
            event.eventType
        }
    }
}