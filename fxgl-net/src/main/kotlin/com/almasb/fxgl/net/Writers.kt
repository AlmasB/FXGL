/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net

import com.almasb.fxgl.core.serialization.Bundle
import java.io.ObjectOutputStream
import java.io.OutputStream

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

object Writers {
    val map = hashMapOf<Class<*>, WriterFactory<*>>()

    init {
        addWriter(Bundle::class.java, WriterFactory { BundleMessageWriter(it) })
        addWriter(ByteArray::class.java, WriterFactory { ByteArrayMessageWriter(it) })
    }

    fun <T> addWriter(type: Class<T>, factory: WriterFactory<*>) {
        map[type] = factory
    }

    fun <T> getWriter(type: Class<T>, out: OutputStream): MessageWriter<T> {
        return map[type]!!.create(out) as MessageWriter<T>
    }
}

class BundleMessageWriter(out: OutputStream) : MessageWriter<Bundle> {

    private val out = ObjectOutputStream(out)

    override fun write(message: Bundle) {
        out.writeObject(message)
    }
}

class ByteArrayMessageWriter(private val out: OutputStream) : MessageWriter<ByteArray> {

    override fun write(message: ByteArray) {
        out.write(message)
    }
}