/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.service.impl.executor

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.service.Executor
import com.google.inject.Inject
import javafx.util.Duration
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Uses cached thread pool to run tasks in the background.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGLExecutor
@Inject
private constructor() : Executor {

    private val service = Executors.newCachedThreadPool(FXGLThreadFactory)
    private val schedulerService = Executors.newScheduledThreadPool(2)

    init {
        FXGL.getApp().addExitListener {
            service.shutdownNow()
            schedulerService.shutdownNow()
        }
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

    /**
     * The default FXGL thread factory.
     */
    private object FXGLThreadFactory : ThreadFactory {
        private val group: ThreadGroup
        private val threadNumber = AtomicInteger(1)

        init {
            val s = System.getSecurityManager()
            group = if (s != null)
                        s.threadGroup
                    else
                        Thread.currentThread().threadGroup
        }

        override fun newThread(r: Runnable): Thread {
            val t = Thread(group, r, "FXGL Background Thread " + threadNumber.andIncrement, 0)

            if (t.isDaemon)
                t.isDaemon = false
            if (t.priority != Thread.NORM_PRIORITY)
                t.priority = Thread.NORM_PRIORITY
            return t
        }
    }
}