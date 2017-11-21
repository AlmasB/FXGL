/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.event;

import javafx.event.Event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method handles an event of specific type.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Handles {

    /**
     * If it's an EntityEvent, then the class is EntityEvent.class,
     * so if you define your own event class, it'll be MyEvent.class.
     * This member can be omitted if the marked method parameter type
     * matches the event class.
     *
     * @return event class
     */
    Class<?> eventClass() default Event.class;

    /**
     * If you have an event type MyGameEvent.ANY,
     * then event type is "ANY".
     * EventType object has to be static in the class
     * where it is declared.
     *
     * @return field name of the EventType object
     */
    String eventType();
}
