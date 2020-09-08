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

interface TCPMessageReader<T> {

    @Throws(Exception::class)
    fun read(): T
}

// we need a factory for tcp message readers since each inpu stream is valid for a single connection
// therefore we cannot reuse the same message reader for each connection
interface TCPReaderFactory<T> {
    fun create(input: InputStream): TCPMessageReader<T>
}

interface UDPMessageReader<T> {
    fun read(data: ByteArray): T
}

object Readers {
    private val log = Logger.get(javaClass)

    private val tcpReaders = hashMapOf<Class<*>, TCPReaderFactory<*>>()
    private val udpReaders = hashMapOf<Class<*>, UDPMessageReader<*>>()

    init {
        addTCPReader(Bundle::class.java, object : TCPReaderFactory<Bundle> {
            override fun create(input: InputStream): TCPMessageReader<Bundle> = BundleTCPMessageReader(input)
        })

        addTCPReader(ByteArray::class.java, object : TCPReaderFactory<ByteArray> {
            override fun create(input: InputStream): TCPMessageReader<ByteArray> = ByteArrayTCPMessageReader(input)
        })

        addTCPReader(String::class.java, object : TCPReaderFactory<String> {
            override fun create(input: InputStream): TCPMessageReader<String> = StringTCPMessageReader(input)
        })

        addUDPReader(Bundle::class.java, BundleUDPMessageReader())
    }

    fun <T> addTCPReader(type: Class<T>, factory: TCPReaderFactory<T>) {
        tcpReaders[type] = factory
    }

    fun <T> addUDPReader(type: Class<T>, reader: UDPMessageReader<T>) {
        udpReaders[type] = reader
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getTCPReader(type: Class<T>, inputStream: InputStream): TCPMessageReader<T> {
        log.debug("Getting TCPMessageReader for $type")

        val readerFactory = tcpReaders[type] ?: throw RuntimeException("No reader factory for type: $type")

        val reader = readerFactory.create(inputStream) as TCPMessageReader<T>

        log.debug("Constructed MessageReader for $type: " + reader.javaClass.simpleName)

        return reader
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getUDPReader(type: Class<T>): UDPMessageReader<T> {
        log.debug("Getting UDPMessageReader for $type")

        val reader = udpReaders[type] as? UDPMessageReader<T>
                ?: throw RuntimeException("No UDP message reader for type: $type")

        log.debug("Constructed UDPMessageReader for $type: " + reader.javaClass.simpleName)

        return reader
    }
}

class BundleTCPMessageReader(stream: InputStream) : TCPMessageReader<Bundle> {
    private val inputStream = ObjectInputStream(stream)

    override fun read(): Bundle {
        return inputStream.readObject() as Bundle
    }
}

class ByteArrayTCPMessageReader(stream: InputStream) : TCPMessageReader<ByteArray> {
    private val stream = DataInputStream(stream)

    override fun read(): ByteArray {
        val len = stream.readInt()

        val buffer = ByteArray(len)

        var bytesReadSoFar = 0

        while (bytesReadSoFar != len) {
            val bytesReadNow = stream.read(buffer, bytesReadSoFar, len - bytesReadSoFar)

            bytesReadSoFar += bytesReadNow
        }

        return buffer
    }
}

class StringTCPMessageReader(inputStream: InputStream) : TCPMessageReader<String> {

    private val reader = ByteArrayTCPMessageReader(inputStream)

    override fun read(): String {
        val bytes = reader.read()

        return String(bytes, Charsets.UTF_16)
    }
}

class BundleUDPMessageReader : UDPMessageReader<Bundle> {
    override fun read(data: ByteArray): Bundle {
        ObjectInputStream(ByteArrayInputStream(data)).use {
            return it.readObject() as Bundle
        }
    }
}