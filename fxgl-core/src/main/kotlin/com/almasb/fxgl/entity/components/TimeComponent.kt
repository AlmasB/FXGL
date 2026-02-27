/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components

import com.almasb.fxgl.entity.component.CopyableComponent

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TimeComponent
@JvmOverloads constructor(value: Double = 1.0) : DoubleComponent(value), CopyableComponent<TimeComponent> {

    override fun isComponentInjectionRequired(): Boolean = false

    override fun copy(): TimeComponent = TimeComponent(value)
}