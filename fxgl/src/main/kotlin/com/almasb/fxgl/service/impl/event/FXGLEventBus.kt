/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.service.impl.event

import com.almasb.fxgl.annotation.Handles
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.core.event.FXEventBus
import com.almasb.fxgl.core.event.Subscriber
import com.almasb.fxgl.service.EventBus
import com.google.inject.Inject
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import java.lang.reflect.Modifier

/**
 * FXGL event dispatcher that uses JavaFX event system to delegate method calls.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGLEventBus
@Inject
private constructor() : EventBus {

    private val log = FXGL.getLogger(javaClass)

    private val bus = FXEventBus()

    override fun <T : Event> addEventHandler(eventType: EventType<T>, eventHandler: EventHandler<in T>): Subscriber {
        return bus.addEventHandler(eventType, eventHandler)
    }

    override fun <T : Event> removeEventHandler(eventType: EventType<T>, eventHandler: EventHandler<in T>) {
        bus.removeEventHandler(eventType, eventHandler)
    }

    override fun fireEvent(event: Event) {
        log.debug { "Firing event: $event" }

        bus.fireEvent(event)
    }

    override fun scanForHandlers(instance: Any) {
        for (method in instance.javaClass.declaredMethods) {
            val annotation = method.getDeclaredAnnotation(Handles::class.java)

            // method is marked @Handles
            if (annotation != null) {

                if (method.parameterTypes.isEmpty() || method.parameterTypes.size > 1) {
                    throw IllegalArgumentException("Method ${method.name} must have a single parameter of type Event or subtype")
                }

                val eventClass: Class<*> = if (annotation.eventClass == Event::class) {
                    // default is used, so get class from method param type
                    method.parameterTypes[0]
                } else {
                    annotation.eventClass.java
                }

                val eventTypeObject = (eventClass.declaredFields
                        // find by name and static modifier
                        .find { it.name == annotation.eventType && Modifier.isStatic(it.modifiers) }
                        // fail if null
                        ?.get(null) ?: throw IllegalArgumentException("<${annotation.eventType}> public static field not found in ${eventClass}"))
                        // ensure that it's EventType
                        as? EventType<*> ?: throw IllegalArgumentException("<${annotation.eventType}> is not of type EventType<*> in ${eventClass}")

                addEventHandler(eventTypeObject, { method.invoke(instance, it) })
            }
        }
    }
}