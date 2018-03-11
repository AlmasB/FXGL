/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.collection

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@FunctionalInterface
interface PropertyChangeListener<in T> {

    fun onChange(prev: T, now: T)
}