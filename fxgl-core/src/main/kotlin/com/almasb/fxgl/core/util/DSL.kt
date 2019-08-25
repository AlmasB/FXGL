/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */


package com.almasb.fxgl.core.util

import com.almasb.fxgl.core.reflect.ReflectionUtils

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

/**
 * Calls [func], if exception occurs in [func] the root cause is thrown.
 */
fun <T> tryCatchRoot(func: Supplier<T>): T {
    try {
        return func.get()
    } catch (e: Exception) {
        throw ReflectionUtils.getRootCause(e)
    }
}

/**
 * Calls [func], if exception occurs in [func] the root cause is thrown.
 */
fun <T> tryCatchRoot(func: () -> T): T {
    try {
        return func.invoke()
    } catch (e: Exception) {
        throw ReflectionUtils.getRootCause(e)
    }
}