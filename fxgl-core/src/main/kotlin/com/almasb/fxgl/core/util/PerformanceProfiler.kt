/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.util

import com.almasb.sslogger.Logger

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class PerformanceProfiler {

    companion object {
        private val map = LinkedHashMap<String, Long>()

        private val log = Logger.get<PerformanceProfiler>()

        fun reset() {
            map.clear()
        }

        @JvmStatic fun start(name: String) {
            println("Starting $name")

            map[name] = System.nanoTime()
        }

        @JvmStatic fun end(name: String) {
            val time = System.nanoTime() - map[name]!!

            println("%s took: %.3f sec".format(name, time / 1000000000.0))
        }

        fun print() {
            map.forEach { name, time ->
                println("$name took: ${time / 1000000000.0} sec")
            }
        }
    }


}