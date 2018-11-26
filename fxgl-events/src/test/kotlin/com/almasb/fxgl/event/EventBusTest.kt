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
                },

                Executable {
                    // add again and remove, but this time using unsubscribe
                    val sub = eventBus.addEventHandler(EventType.ROOT, handler)

                    eventBus.fireEvent(Event(EventType.ROOT))

                    assertThat(count, `is`(2))

                    sub.unsubscribe()

                    eventBus.fireEvent(Event(EventType.ROOT))

                    assertThat(count, `is`(2))
                }
        )
    }
}