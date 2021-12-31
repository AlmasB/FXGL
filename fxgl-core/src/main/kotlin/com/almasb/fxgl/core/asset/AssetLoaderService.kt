/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.asset

import com.almasb.fxgl.core.EngineService
import java.net.URL

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class AssetLoaderService : EngineService() {

    abstract fun <T> load(assetType: AssetType, fileName: String): T

    abstract fun <T> load(assetType: AssetType, url: URL): T

    /**
     * @return URL to an asset file, [name] (relative) must start with /assets/
     */
    abstract fun getURL(name: String): URL
}