/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.concurrent

import javafx.util.Duration
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Uses cached thread pool to run tasks in the background.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGLExecutor : Executor {

    companion object {
        val service = Executors.newCachedThreadPool(FXGLThreadFactory)
        private val schedulerService = Executors.newScheduledThreadPool(2, FXGLThreadFactory)
    }

    override fun execute(task: Runnable) {
        service.submit(task)
    }

    override fun schedule(action: Runnable, delay: Duration): ScheduledFuture<*> {
        return schedulerService.schedule(action, delay.toMillis().toLong(), TimeUnit.MILLISECONDS)
    }

    override fun <T : Any> async(func: Callable<T>): Async<T> {
        return Async.start(func)
    }

    override fun async(func: Runnable): Async<Void> {
        return Async.start(func)
    }

    override fun shutdownNow() {
        service.shutdownNow()
        schedulerService.shutdownNow()
    }

    /**
     * The default FXGL thread factory.
     */
    private object FXGLThreadFactory : ThreadFactory {
        private val threadNumber = AtomicInteger(1)

        override fun newThread(r: Runnable): Thread {
            val t = Thread(r, "FXGL Background Thread " + threadNumber.andIncrement)
            t.isDaemon = false
            t.priority = Thread.NORM_PRIORITY
            return t
        }
    }
}