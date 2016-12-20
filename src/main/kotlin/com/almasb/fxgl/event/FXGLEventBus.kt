/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.event

import com.almasb.fxeventbus.FXEventBus
import com.almasb.fxeventbus.Subscriber
import com.almasb.fxgl.app.FXGL
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

    init {
        log.debug { "Service [EventBus] initialized" }
    }

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