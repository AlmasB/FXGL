/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dev.profiling

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.scene.SceneService

/**
 * This service provides access to a range of profiling tools,
 * including CPU time it took to compute last frame, FPS and RAM.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ProfilerService : EngineService() {

    companion object {
        private val runtime = Runtime.getRuntime()

        private const val MB = 1024.0 * 1024.0
    }

    private lateinit var sceneService: SceneService

    private var usedRAM = 0L
    private var gcRuns = 0

    private lateinit var cpuProfilerWindow: ProfilerWindow
    private lateinit var fpsProfilerWindow: ProfilerWindow
    private lateinit var ramProfilerWindow: ProfilerWindow

    override fun onInit() {
        fpsProfilerWindow = ProfilerWindow(300.0, 100.0, "FPS")
        fpsProfilerWindow.numYTicks = 2
        fpsProfilerWindow.preferredMaxValue = 60.0

        cpuProfilerWindow = ProfilerWindow(300.0, 100.0, "CPU (ms)")
        cpuProfilerWindow.numYTicks = 5
        cpuProfilerWindow.preferredMaxValue = 17.0
        cpuProfilerWindow.relocate(300.0, 0.0)

        ramProfilerWindow = ProfilerWindow(300.0, 100.0, "RAM (MB)")
        ramProfilerWindow.numYTicks = 5
        ramProfilerWindow.preferredMaxValue = 300.0
        ramProfilerWindow.relocate(600.0, 0.0)

        sceneService.overlayRoot.children.addAll(
                fpsProfilerWindow,
                cpuProfilerWindow,
                ramProfilerWindow
        )
    }

    override fun onUpdate(tpf: Double) {
        fpsProfilerWindow.update(1.0 / tpf)
        cpuProfilerWindow.update(FXGL.cpuNanoTime() / 1_000_000.0)

        val used = runtime.totalMemory() - runtime.freeMemory()

        // ignore incorrect readings
        if (used < 0)
            return

        if (used < usedRAM) {
            gcRuns++
        }

        usedRAM = used

        ramProfilerWindow.update(usedRAM / MB)
    }

    override fun onExit() {
        val log = Logger.get(javaClass)

        fpsProfilerWindow.log()
        cpuProfilerWindow.log()
        ramProfilerWindow.log()

        log.info("Estimated GC runs: $gcRuns")
    }
}