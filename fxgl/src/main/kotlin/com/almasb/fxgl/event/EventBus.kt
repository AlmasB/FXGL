/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.event

import com.almasb.fxgl.annotation.Handles
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.core.collection.UnorderedArray
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.scene.Group
import java.lang.reflect.Modifier

/**
 * FXGL event dispatcher that uses JavaFX event system to delegate method calls.
 * Manages event dispatching, listening and handling.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class EventBus {

    private val log = FXGL.getLogger(javaClass)

    private val eventTriggers = UnorderedArray<EventTrigger<*>>(32)

    private val eventHandlers = Group()

    fun onUpdate(tpf: Double) {
        updateTriggers(tpf)
    }

    private fun updateTriggers(tpf: Double) {
        val it = eventTriggers.iterator()
        while (it.hasNext()) {
            val trigger = it.next()

            trigger.onUpdate(tpf)

            if (trigger.reachedLimit()) {
                it.remove()
            }
        }
    }

    fun addEventTrigger(trigger: EventTrigger<*>) {
        eventTriggers.add(trigger)
    }

    fun removeEventTrigger(trigger: EventTrigger<*>) {
        eventTriggers.removeValueByIdentity(trigger)
    }

    /**
     * Register event handler for event type.
     */
    fun <T : Event> addEventHandler(eventType: EventType<T>, eventHandler: EventHandler<in T>): Subscriber {
        eventHandlers.addEventHandler(eventType, eventHandler)
        return Subscriber(this, eventType, eventHandler as EventHandler<in Event>)
    }

    /**
     * Remove event handler for event type.
     */
    fun <T : Event> removeEventHandler(eventType: EventType<T>, eventHandler: EventHandler<in T>) {
        eventHandlers.removeEventHandler(eventType, eventHandler)
    }

    /**
     * Post (fire) given event. All listening parties will be notified.
     * Events will be handled on the same thread that fired the event,
     * i.e. synchronous.
     *
     * <p>
     *     Note: according to JavaFX doc this must be called on JavaFX Application Thread.
     *     In reality this doesn't seem to be true.
     * </p>
     *
     * @param event the event
     */
    fun fireEvent(event: Event) {
        log.debug { "Firing event: $event" }

        eventHandlers.fireEvent(event)
    }

    /**
     * Scan an object for public methods marked @Handles
     * and add them to the event bus.
     *
     * @param instance object to scan
     * @throws IllegalArgumentException if syntax error during scan
     */
    fun scanForHandlers(instance: Any) {
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

                addEventHandler(eventTypeObject, EventHandler { method.invoke(instance, it) })
            }
        }
    }
}