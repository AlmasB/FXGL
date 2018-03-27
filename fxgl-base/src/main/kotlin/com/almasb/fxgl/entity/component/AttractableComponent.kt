/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.component

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