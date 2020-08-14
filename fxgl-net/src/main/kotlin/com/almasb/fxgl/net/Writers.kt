/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net

import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.net.Protocol.*
import java.io.*
import java.lang.RuntimeException

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

object Writers {
    private val log = Logger.get(javaClass)

    private val tcpWriters = hashMapOf<Class<*>, WriterFactory<*>>()
    private val udpWriters = hashMapOf<Class<*>, WriterFactory<*>>()

    init {
        addWriter(TCP, Bundle::class.java, WriterFactory { BundleMessageWriter(it) })
        addWriter(TCP, ByteArray::class.java, WriterFactory { ByteArrayMessageWriter(it) })
        addWriter(TCP, String::class.java, WriterFactory { StringMessageWriter(it) })
    }

    fun <T> addWriter(protocol: Protocol, type: Class<T>, factory: WriterFactory<*>) {
        when (protocol) {
            TCP -> tcpWriters[type] = factory
            UDP -> udpWriters[type] = factory
        }
    }

    fun <T> getWriter(protocol: Protocol, type: Class<T>, out: OutputStream): MessageWriter<T> {
        log.debug("Getting MessageWriter for $protocol-$type")

        val writerFactory = when (protocol) {
            TCP -> tcpWriters[type] ?: throw RuntimeException("No message writer factory for type: $protocol-$type")
            UDP -> udpWriters[type] ?: throw RuntimeException("No message writer factory for type: $protocol-$type")
        }

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

class ByteArrayMessageWriter(out: OutputStream) : MessageWriter<ByteArray> {

    private val out = DataOutputStream(out)

    override fun write(message: ByteArray) {
        out.writeInt(message.size)
        out.write(message)
    }
}

class StringMessageWriter(out: OutputStream) : MessageWriter<String> {

    private val out = PrintWriter(out, true)

    override fun write(message: String) {
        out.write(message)
    }
}