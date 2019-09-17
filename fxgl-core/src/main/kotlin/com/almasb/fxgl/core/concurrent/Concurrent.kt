/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */


package com.almasb.fxgl.core.concurrent

import javafx.application.Platform
import javafx.util.Duration
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Implementation details of concurrent tasks.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

object Async : Executor {

    private val service = Executors.newCachedThreadPool(FXGLThreadFactory)
    private val schedulerService = Executors.newScheduledThreadPool(2, FXGLThreadFactory)

    override fun execute(task: Runnable) {
        service.submit(task)
    }

    override fun schedule(action: Runnable, delay: Duration): ScheduledFuture<*> {
        return schedulerService.schedule(action, delay.toMillis().toLong(), TimeUnit.MILLISECONDS)
    }

    override fun <T : Any> startAsync(func: Callable<T>): AsyncTask<T> = Coroutine(func, service)

    override fun startAsync(func: Runnable): AsyncTask<Void> = Coroutine(Callable<Void> {
        func.run()
        return@Callable null
    }, service)

    override fun <T : Any> startAsyncFX(func: Callable<T>): AsyncTask<T> = FXCoroutine(func)

    override fun startAsyncFX(func: Runnable): AsyncTask<Void> = FXCoroutine(Callable<Void> {
        func.run()
        return@Callable null
    })

    override fun shutdownNow() {
        service.shutdownNow()
        schedulerService.shutdownNow()
    }
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

private class Coroutine<T>(private val func: Callable<T>,
                   private val service: ExecutorService) : AsyncTask<T>() {

    private val latch = CountDownLatch(1)

    private var value: T? = null

    init {
        service.submit {
            try {
                value = func.call()
            } catch (e: Exception) {

                // re-throw to jfx thread so it knows we are going to crash
                Platform.runLater {
                    throw e
                }
            } finally {
                latch.countDown()
            }
        }
    }

    override fun await(): T? {
        latch.await()
        return value
    }
}

private class FXCoroutine<T>(private val func: Callable<T>) : AsyncTask<T>() {

    private val latch = CountDownLatch(1)

    private var value: T? = null

    init {
        if (Platform.isFxApplicationThread()) {
            try {
                value = func.call()
            } finally {
                latch.countDown()
            }
        } else {
            Platform.runLater {
                try {
                    value = func.call()
                } finally {
                    latch.countDown()
                }
            }
        }
    }

    override fun await(): T? {
        latch.await()
        return value
    }
}