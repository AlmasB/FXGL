/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.asset

import com.almasb.fxgl.core.EngineService

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class AssetLoaderService : EngineService() {

    abstract fun <T> load(assetType: AssetType, fileName: String): T
}