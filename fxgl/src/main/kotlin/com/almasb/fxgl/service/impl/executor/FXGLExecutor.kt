/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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