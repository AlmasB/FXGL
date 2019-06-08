/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.entity.component.Component

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class OffscreenPauseComponent : Component() {
    private val viewport = FXGL.getGameScene().viewport

    override fun onUpdate(tpf: Double) {
        if (entity.boundingBoxComponent.isOutside(viewport.visibleArea)) {
            // TODO:
        }
    }
}