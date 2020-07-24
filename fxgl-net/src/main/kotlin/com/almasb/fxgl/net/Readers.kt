/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net

import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.logging.Logger
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.ObjectInputStream


/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

object Readers {
    private val log = Logger.get(javaClass)

    private val map = hashMapOf<Class<*>, ReaderFactory<*>>()

    init {
        addReader(Bundle::class.java, ReaderFactory { BundleMessageReader(it) })
        addReader(ByteArray::class.java, ReaderFactory { ByteArrayMessageReader(it) })
        addReader(String::class.java, ReaderFactory { StringMessageReader(it) })
    }

    fun <T> addReader(type: Class<T>, factory: ReaderFactory<T>) {
        map[type] = factory
    }

    fun <T> getReader(type: Class<T>, inputStream: InputStream): MessageReader<T> {
        log.debug("Getting MessageReader for $type")

        val readerFactory = map[type] ?: throw RuntimeException("No reader factory for type: $type")

        val reader = readerFactory.create(inputStream) as MessageReader<T>

        log.debug("Constructed MessageReader for $type: " + reader.javaClass.simpleName)

        return reader
    }
}

class BundleMessageReader(stream: InputStream) : MessageReader<Bundle> {
    private val inputStream = ObjectInputStream(stream)

    override fun read(): Bundle {
        return inputStream.readObject() as Bundle
    }
}

class ByteArrayMessageReader(private val stream: InputStream) : MessageReader<ByteArray> {
    override fun read(): ByteArray {

        val result = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var length: Int

        while (stream.read(buffer).also { length = it } != -1) {
            result.write(buffer, 0, length)

            if (length < buffer.size) {
                break
            }
        }

        return result.toByteArray()
    }
}

class StringMessageReader(inputStream: InputStream) : MessageReader<String> {

    private val inputStream = InputStreamReader(inputStream, Charsets.UTF_8)

    override fun read(): String {

        val bufferSize = 1024
        val buffer = CharArray(bufferSize)
        val out = StringBuilder()
        var charsRead: Int

        while (inputStream.read(buffer).also { charsRead = it } >= 0) {
            out.append(buffer, 0, charsRead)

            println("SB: ${out.toString()}")
        }
        return out.toString()


//        val result = ByteArrayOutputStream()
//        val buffer = ByteArray(1024)
//        var length: Int
//
//        while (inputStream.read(buffer).also { length = it } != -1) {
//            result.write(buffer, 0, length)
//        }
//
//        return result.toString(Charsets.UTF_8)
    }
}