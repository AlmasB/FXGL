/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.concurrent

import com.almasb.fxgl.core.EngineService
import java.util.concurrent.CompletableFuture

/**
 *
 * @author Jean-Rene Lavoie (jeanrlavoie@gmail.com)
 */
abstract class AsyncService<T> : EngineService() {

    private var asyncTask: CompletableFuture<T>? = null

    /**
     * Call the async game update. On next game update, wait for the task to be completed (if not already) and
     * call onPostGameUpdateAsync to allow JavaFX thread dependent task handling (e.g. updating the Nodes)
     */
    override fun onGameUpdate(tpf: Double) {
        asyncTask?.let { onPostGameUpdateAsync(it.get()) }
        asyncTask = CompletableFuture.supplyAsync() { onGameUpdateAsync(tpf) } // Process until next onGameUpdate
    }

    /**
     * Async game update processing method.
     * Warning: This will not run on the main JavaFX thread. This means that any changes done on the Nodes will cause
     * an exception.
     */
    abstract fun onGameUpdateAsync(tpf: Double): T

    /**
     * Async processing Callback. This method is called on next onGameUpdate allowing synchronization between this
     * Service async processing and the main JavaFX thread.
     */
    open fun onPostGameUpdateAsync(result: T) { }
}