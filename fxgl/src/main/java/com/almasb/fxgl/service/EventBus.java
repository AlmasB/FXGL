/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.service;

/**
 * Service for event dispatching, listening and handling.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface EventBus extends com.almasb.fxgl.core.event.EventBus {

    /**
     * Scan an object for public methods marked @Handles
     * and add them to the event bus.
     *
     * @param instance object to scan
     * @throws IllegalArgumentException if syntax error during scan
     */
    void scanForHandlers(Object instance);
}
