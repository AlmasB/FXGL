/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.extra.entity.components

import com.almasb.fxgl.entity.components.DoubleComponent

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AttractableComponent(

        /**
         * Higher resistance leads weaker attraction of entity by attractors.
         */
        resistance: Double) : DoubleComponent(resistance) {
}