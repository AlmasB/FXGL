/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net

import com.almasb.fxgl.core.concurrent.IOTask
import java.io.InputStream

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FXGLNetService : NetService() {

    override fun openStreamTask(url: String): IOTask<InputStream> {
        return TODO()
    }
}