/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.concurrent

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import java.util.concurrent.Callable

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Coroutine<T>(private val func: Callable<T>) : Async<T>() {

    private val deferred: Deferred<T> = async(CommonPool) {
        func.call()
    }

    override fun await() = runBlocking {
        deferred.await()
    }
}