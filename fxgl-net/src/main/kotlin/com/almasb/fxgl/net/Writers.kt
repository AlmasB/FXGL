/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net

import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.logging.Logger
import java.io.*
import java.lang.RuntimeException

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

object Writers {
    private val log = Logger.get(javaClass)

    private val map = hashMapOf<Class<*>, WriterFactory<*>>()

    init {
        addWriter(Bundle::class.java, WriterFactory { BundleMessageWriter(it) })
        addWriter(ByteArray::class.java, WriterFactory { ByteArrayMessageWriter(it) })
        addWriter(String::class.java, WriterFactory { StringMessageWriter(it) })
    }

    fun <T> addWriter(type: Class<T>, factory: WriterFactory<*>) {
        map[type] = factory
    }

    fun <T> getWriter(type: Class<T>, out: OutputStream): MessageWriter<T> {
        log.debug("Getting MessageWriter for $type")

        val writerFactory = map[type] ?: throw RuntimeException("No message writer factory for type: $type")

        val writer = writerFactory.create(out) as MessageWriter<T>

        log.debug("Constructed MessageWriter for $type: " + writer.javaClass.simpleName)

        return writer
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

class StringMessageWriter(out: OutputStream) : MessageWriter<String> {

    private val out = PrintWriter(out, true)

    override fun write(message: String) {
        out.write(message)
    }
}