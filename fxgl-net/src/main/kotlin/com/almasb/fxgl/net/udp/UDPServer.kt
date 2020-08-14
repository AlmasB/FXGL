/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net.udp

import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.net.Server
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.util.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class UDPServer<T>(val port: Int, private val messageType: Class<T>) : Server<T>() {

    private val log = Logger.get(javaClass)

    private var isStopped = false

    override fun start() {
        log.debug("Starting to listen at: $port type: $messageType")

        try {
            DatagramSocket(port).use {

                onStartedListening()

                var connectionNum = 1

                val buffer = ByteArray(2048)

                while (!isStopped) {

                    Arrays.fill(buffer, 0)

                    val packet = DatagramPacket(buffer, buffer.size)

                    it.receive(packet)

                    val remoteIP = packet.address.hostAddress
                    val remotePort = packet.port
                    val fullIP = remoteIP + remotePort

                    var connection = connections.map { it as UDPConnection }.find { it.fullIP == fullIP }

                    if (connection == null) {
                        connection = UDPConnection<T>(fullIP, connectionNum++)

                        openUDPConnection(connection)
                    }

                    connection.receive(packet.data)
                }
            }

        } catch (e: Exception) {
            // TODO: check logic here

            if (!isStopped) {
                throw RuntimeException("Failed to start: " + e.message, e)
            }
        }

        onStoppedListening()
    }

    override fun stop() {
        isStopped = true

        // TODO: send closing?
    }
}