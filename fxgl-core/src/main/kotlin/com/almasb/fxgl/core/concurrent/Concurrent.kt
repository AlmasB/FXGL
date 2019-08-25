/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */


package com.almasb.fxgl.core.concurrent

import javafx.application.Platform
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

class Coroutine<T>(private val func: Callable<T>,
                   private val service: ExecutorService) : Async<T>() {

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

class FXCoroutine<T>(private val func: Callable<T>) : Async<T>() {

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