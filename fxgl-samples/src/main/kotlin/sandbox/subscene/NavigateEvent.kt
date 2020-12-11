/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.subscene

import javafx.event.Event
import javafx.event.EventType

val NAVIGATION: EventType<NavigateEvent> = EventType(Event.ANY, "NAVIGATION")

val MAIN_VIEW: EventType<NavigateEvent> = EventType(NAVIGATION, "MAIN")

val OPTIONS_VIEW: EventType<NavigateEvent> = EventType(NAVIGATION, "OPTIONS")

val ABOUT_VIEW: EventType<NavigateEvent> = EventType(NAVIGATION, "ABOUT")

val PLAY_VIEW: EventType<NavigateEvent> = EventType(NAVIGATION, "PLAY")


class NavigateEvent(eventType: EventType<out Event?>?) : Event(eventType)
