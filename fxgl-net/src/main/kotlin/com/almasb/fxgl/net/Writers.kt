/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net

import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.logging.Logger
import java.io.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

interface TCPMessageWriter<T> {

    @Throws(Exception::class)
    fun write(message: T)
}

// we need a factory for tcp message writers since each output stream is valid for a single connection
// therefore we cannot reuse the same message writer for each connection
interface TCPWriterFactory<T> {

    fun create(out: OutputStream): TCPMessageWriter<T>
}

interface UDPMessageWriter<T> {
    fun write(data: T): ByteArray
}

object Writers {
    private val log = Logger.get(javaClass)

    private val tcpWriters = hashMapOf<Class<*>, TCPWriterFactory<*>>()
    private val udpWriters = hashMapOf<Class<*>, UDPMessageWriter<*>>()

    init {
        // these are built-in writers
        addTCPWriter(Bundle::class.java, object : TCPWriterFactory<Bundle> {
            override fun create(out: OutputStream): TCPMessageWriter<Bundle> = BundleTCPMessageWriter(out)
        })

        addTCPWriter(ByteArray::class.java, object : TCPWriterFactory<ByteArray> {
            override fun create(out: OutputStream): TCPMessageWriter<ByteArray> = ByteArrayTCPMessageWriter(out)
        })

        addTCPWriter(String::class.java, object : TCPWriterFactory<String> {
            override fun create(out: OutputStream): TCPMessageWriter<String> = StringTCPMessageWriter(out)
        })

        addUDPWriter(Bundle::class.java, BundleUDPMessageWriter())
    }

    fun <T> addTCPWriter(type: Class<T>, factory: TCPWriterFactory<T>) {
        tcpWriters[type] = factory
    }

    fun <T> addUDPWriter(type: Class<T>, writer: UDPMessageWriter<T>) {
        udpWriters[type] = writer
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getTCPWriter(type: Class<T>, out: OutputStream): TCPMessageWriter<T> {
        log.debug("Getting TCPMessageWriter for $type")

        val writerFactory = tcpWriters[type]
                ?: throw RuntimeException("No TCP message writer factory for type: $type")

        val writer = writerFactory.create(out) as TCPMessageWriter<T>

        log.debug("Constructed TCPMessageWriter for $type: " + writer.javaClass.simpleName)

        return writer
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getUDPWriter(type: Class<T>): UDPMessageWriter<T> {
        log.debug("Getting UDPMessageWriter for $type")

        val writer = udpWriters[type] as? UDPMessageWriter<T>
                ?: throw RuntimeException("No UDP message writer for type: $type")

        log.debug("Constructed UDPMessageWriter for $type: " + writer.javaClass.simpleName)

        return writer
    }
}

class BundleTCPMessageWriter(out: OutputStream) : TCPMessageWriter<Bundle> {

    private val out = ObjectOutputStream(out)

    override fun write(message: Bundle) {
        out.writeObject(message)
    }
}

class ByteArrayTCPMessageWriter(out: OutputStream) : TCPMessageWriter<ByteArray> {

    private val out = DataOutputStream(out)

    override fun write(message: ByteArray) {
        out.writeInt(message.size)
        out.write(message)
    }
}

class StringTCPMessageWriter(out: OutputStream) : TCPMessageWriter<String> {

    private val writer = ByteArrayTCPMessageWriter(out)

    override fun write(message: String) {
        writer.write(message.toByteArray(Charsets.UTF_16))
    }
}

class BundleUDPMessageWriter : UDPMessageWriter<Bundle> {
    override fun write(data: Bundle): ByteArray {
        return toByteArray(data)
    }

    private fun toByteArray(data: Serializable): ByteArray {
        val baos = ByteArrayOutputStream()
        ObjectOutputStream(baos).use { it.writeObject(data) }
        return baos.toByteArray()
    }
}