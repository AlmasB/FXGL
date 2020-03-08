/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.event

import com.almasb.fxgl.core.EngineService

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EventBusService : EngineService() {

    val eventBus = EventBus()
}