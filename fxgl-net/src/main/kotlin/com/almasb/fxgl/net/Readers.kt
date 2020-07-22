/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net

import com.almasb.fxgl.core.serialization.Bundle
import java.io.InputStream
import java.io.ObjectInputStream

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

object Readers {
    val map = hashMapOf<Class<*>, ReaderFactory<*>>()

    init {
        addReader(Bundle::class.java, ReaderFactory { BundleMessageReader(it) })
    }

    fun <T> addReader(type: Class<T>, factory: ReaderFactory<T>) {
        map[type] = factory
    }

    fun <T> getReader(type: Class<T>, inputStream: InputStream): MessageReader<T> {
        return map[type]!!.create(inputStream) as MessageReader<T>
    }
}


class BundleMessageReader(stream: InputStream) : MessageReader<Bundle> {
    private val inputStream = ObjectInputStream(stream)

    override fun read(): Bundle {
        return inputStream.readObject() as Bundle
    }
}