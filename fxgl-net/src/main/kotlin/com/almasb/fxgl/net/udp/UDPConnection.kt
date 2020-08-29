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
import java.util.*
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

        sendUDP(MESSAGE_CLOSE)
    }

    fun sendUDP(data: ByteArray) {
        val packet = DatagramPacket(data, data.size,
                InetAddress.getByName(remoteIp), remotePort
        )

        socket.send(packet)
    }

    internal fun receive(data: ByteArray) {
        // TODO: what if this is not fully received
        // to fix, use 1st message as length (int - 4bytes) of actual 2nd message
        // when done add to queue

        // make a copy to avoid the buffer being overwritten
        val copy = Arrays.copyOf(data, data.size)

        recvQueue.put(copy)
    }
}