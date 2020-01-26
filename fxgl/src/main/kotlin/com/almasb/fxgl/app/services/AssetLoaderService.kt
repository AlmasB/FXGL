/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.services

import com.almasb.fxgl.app.AssetLoader
import com.almasb.fxgl.core.EngineService

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AssetLoaderService : EngineService() {

    val assetLoader = AssetLoader()
}