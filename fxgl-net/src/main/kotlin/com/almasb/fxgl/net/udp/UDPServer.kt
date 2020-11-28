/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net.udp

import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.net.Server
import com.almasb.fxgl.net.UDPServerConfig
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.util.*
import java.util.Arrays.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class UDPServer<T>(val port: Int, private val config: UDPServerConfig<T>) : Server<T>() {

    private val log = Logger.get(javaClass)

    private var isStopped = false

    private var serverSocket: DatagramSocket? = null

    override fun start() {
        log.debug("Starting to listen at: $port type: ${config.messageType}")

        try {
            DatagramSocket(port).use {
                serverSocket = it

                onStartedListening()

                var connectionNum = 1

                val buffer = ByteArray(config.bufferSize)

                while (!isStopped) {

                    fill(buffer, 0)

                    val packet = DatagramPacket(buffer, buffer.size)

                    it.receive(packet)

                    val remoteIP = packet.address.hostAddress
                    val remotePort = packet.port
                    val fullIP = remoteIP + remotePort

                    var connection = connections.map { it as UDPConnection }.find { it.fullIP == fullIP }

                    // check if packet contains MESSAGE_OPEN
                    val isOpeningPacket = equals(copyOfRange(packet.data, 0, MESSAGE_OPEN.size), MESSAGE_OPEN)

                    if (connection == null || isOpeningPacket) {
                        connection = UDPConnection<T>(it, remoteIP, remotePort, config.bufferSize, connectionNum++)

                        openUDPConnection(connection, config.messageType)
                    }

                    val isClosingPacket = equals(copyOfRange(packet.data, 0, MESSAGE_CLOSE.size), MESSAGE_CLOSE)

                    if (isClosingPacket) {
                        onConnectionClosed(connection)
                    }

                    if (!isOpeningPacket && !isClosingPacket) {
                        connection.receive(packet.data)
                    }
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

    // TODO: extract into common between TCPServer
    override fun stop() {
        if (isStopped) {
            log.warning("Attempted to stop a server that is already stopped")
            return
        }

        isStopped = true

        // for UDP we also terminate all active connections
        connections.forEach {
            it.terminate()
        }

        try {
            serverSocket?.close()
        } catch (e: Exception) {
            log.warning("Exception when closing server socket: " + e.message, e)
        }
    }
}