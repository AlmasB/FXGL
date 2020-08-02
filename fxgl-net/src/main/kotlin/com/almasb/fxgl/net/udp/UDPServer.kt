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

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class UDPServer<T>(val port: Int, private val messageType: Class<T>) : Server<T>() {

    private val log = Logger.get(javaClass)

    private var isStopped = false

    override fun start() {
        log.debug("Starting to listen at: $port type: $messageType")

        // TODO: while (!isStopped) {}

        try {
            DatagramSocket(port).use {

                onStartedListening()

                val buffer = ByteArray(1024)

                val packet = DatagramPacket(buffer, buffer.size)

                it.receive(packet)


                var connectionNum = 1

                // TODO: check if address already exists (i.e. connection exists)
                // re-route the packet to that connection
                // "Connection" should probably be an abstraction
                // openNewConnection(socket, connectionNum++, messageType);
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
        TODO()
    }
}