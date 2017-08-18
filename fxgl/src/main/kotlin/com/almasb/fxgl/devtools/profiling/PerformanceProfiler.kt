/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.devtools.profiling

import com.almasb.fxgl.core.logging.Logger

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class PerformanceProfiler {

    private var nanos = 0L

    fun start() {
        nanos = System.nanoTime()
    }

    fun stop() {
        nanos = System.nanoTime() - nanos
        Logger.get(javaClass).info("Took: %.3f sec".format(nanos / 1000000000.0))
    }
}