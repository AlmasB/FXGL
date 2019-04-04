/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core

import javafx.scene.Group

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface VisualEngineService : EngineService {

    /**
     * [overlayRoot] is part of the active scene graph to which
     * visual elements of this service can be attached.
     */
    fun provideOverlayRoot(overlayRoot: Group)
}