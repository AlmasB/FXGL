/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.component

import com.almasb.fxgl.entity.Component

/**
 * Notifies when a component or a control is added / removed.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface ComponentListener {

    fun onAdded(component: Component)

    fun onRemoved(component: Component)
}