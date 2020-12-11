/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net.udp

import com.almasb.fxgl.net.Connection
import com.almasb.fxgl.net.MessageHandler
import javafx.application.Platform
import java.io.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer
import java.util.*
import java.util.Arrays.copyOfRange
import java.util.concurrent.ArrayBlockingQueue
import java.util.function.Consumer


/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class UDPConnection<T>(
        private val socket: DatagramSocket,

        /**
         * Remote ip.
         */
        val remoteIp: String,

        val remotePort: Int,

        /**
         * This connection will never send packets with size larger than buffer size.
         * Larger messages will be automatically deconstructed on this end and reconstructed on the other end.
         */
        private val bufferSize: Int,

        connectionNum: Int) : Connection<T>(connectionNum) {

    val fullIP: String
        get() = remoteIp + remotePort

    private var isClosed = false

    val recvQueue = ArrayBlockingQueue<ByteArray>(30)

    override fun isClosedLocally(): Boolean {
        return isClosed
    }

    override fun terminateImpl() {
        isClosed = true

        // send MESSAGE_CLOSE directly as it is a special message
        val packet = DatagramPacket(MESSAGE_CLOSE, MESSAGE_CLOSE.size)

        socket.send(packet)
    }

    fun sendUDP(data: ByteArray) {
        // use 1st message as length (int - 4bytes) of actual 2nd message

        val sizeAsByteArray = ByteBuffer.allocate(4).putInt(data.size).array()

        val packet = DatagramPacket(sizeAsByteArray, 4,
                InetAddress.getByName(remoteIp), remotePort
        )

        socket.send(packet)

        if (data.size <= bufferSize) {
            socket.send(
                    DatagramPacket(data, data.size, InetAddress.getByName(remoteIp), remotePort)
            )
        } else {
            // deconstruct

            val numChunks = data.size / bufferSize

            repeat(numChunks) { index ->

                val newData = copyOfRange(data, index * bufferSize, index * bufferSize + bufferSize)

                socket.send(
                        DatagramPacket(newData, bufferSize, InetAddress.getByName(remoteIp), remotePort)
                )
            }

            val remainderSize = data.size - numChunks * bufferSize
            val remainderData = copyOfRange(data, data.size - remainderSize, data.size)

            socket.send(
                    DatagramPacket(remainderData, remainderSize, InetAddress.getByName(remoteIp), remotePort)
            )
        }
    }

    private var messageSize = -1
    private var messageBuffer = ByteArray(0)

    private var currentSize = 0

    internal fun receive(data: ByteArray) {
        if (messageSize == -1) {
            // receiving message size as a 4-byte array (aka int)

            messageSize = ByteBuffer.wrap(data).int
            messageBuffer = ByteArray(messageSize)
            return
        }

        // reconstruct
        // we are receiving in a fixed buffer size, but actual data size might be different
        val actualDataSize = minOf(data.size, messageSize - currentSize)

        System.arraycopy(data, 0, messageBuffer, currentSize, actualDataSize)

        currentSize += actualDataSize

        if (currentSize == messageSize) {
            recvQueue.put(messageBuffer)

            currentSize = 0
            messageSize = -1
        }
    }
}