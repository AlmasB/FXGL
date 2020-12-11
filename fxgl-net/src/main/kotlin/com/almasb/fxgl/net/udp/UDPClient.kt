/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net.udp

import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.net.Client
import com.almasb.fxgl.net.UDPClientConfig
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.*

internal val MESSAGE_OPEN = byteArrayOf(-2, -1, 0, 70, 0, 88, 0, 71, 0, 76, 0, 95, 0, 72, 0, 69, 0, 76, 0, 76, 0, 79)
internal val MESSAGE_CLOSE = byteArrayOf(-2, -1, 0, 70, 0, 88, 0, 71, 0, 76, 0, 95, 0, 66, 0, 89, 0, 69, 0, 33, 0, 33)

/**
 * TODO: readers / writers will operate on byte[] <-> T
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class UDPClient<T>(val ip: String, val port: Int, private val config: UDPClientConfig<T>) : Client<T>() {

    private val log = Logger.get(javaClass)

    private var isStopped = false

    private var socket: DatagramSocket? = null

    override fun connect() {
        // TODO: exception handling

        DatagramSocket().use {
            socket = it
            it.connect(InetAddress.getByName(ip), port)

            var connection = connections.firstOrNull() as? UDPConnection

            if (connection == null) {
                connection = UDPConnection<T>(it, ip, port, config.bufferSize, 1)

                openUDPConnection(connection, config.messageType)

                // send opening message to server, so server has our ip and port
                val packet = DatagramPacket(MESSAGE_OPEN, MESSAGE_OPEN.size)

                it.send(packet)
            }

            val buffer = ByteArray(config.bufferSize)

            while (!isStopped) {
                Arrays.fill(buffer, 0)

                val packet = DatagramPacket(buffer, buffer.size)

                it.receive(packet)

                val isClosingPacket = Arrays.equals(Arrays.copyOfRange(packet.data, 0, MESSAGE_CLOSE.size), MESSAGE_CLOSE)

                if (isClosingPacket) {
                    isStopped = true
                    onConnectionClosed(connection)
                } else {
                    connection.receive(packet.data)
                }
            }

            // we manually closed it via [disconnect]
            if (it.isClosed) {
                onConnectionClosed(connection)
            }
        }
    }

    // TODO: extract into common between UDPServer
    override fun disconnect() {
        if (isStopped) {
            log.warning("Attempted to stop a client that is already stopped")
            return
        }

        isStopped = true

        connections.forEach { it.terminate() }

        try {
            socket?.close()
        } catch (e: Exception) {
            log.warning("Exception when closing client socket: " + e.message, e)
        }
    }
}