/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.util

/**
 * A lazy value is only initialized when [get] is invoked.
 * Subsequent calls to [get] returns the same value (instance).
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class LazyValue<T>(private val supplier: Supplier<T>) : Supplier<T> {

    private var value: T? = null

    override fun get(): T {
        if (value == null)
            value = supplier.get()

        return value!!
    }
}