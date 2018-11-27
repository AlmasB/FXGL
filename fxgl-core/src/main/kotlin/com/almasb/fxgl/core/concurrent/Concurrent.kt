package com.almasb.fxgl.core.concurrent

import javafx.application.Platform
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

class Coroutine<T>(private val func: Callable<T>) : Async<T>() {

    private val deferred: Deferred<T> = GlobalScope.async {
        func.call()
    }

    override fun await() = runBlocking {
        deferred.await()
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