/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.event

import javafx.event.Event

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface EventProducer<out T : Event> {

    fun produce(): T
}