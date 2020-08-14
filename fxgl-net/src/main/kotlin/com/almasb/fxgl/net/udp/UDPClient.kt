/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net.udp

import com.almasb.fxgl.net.Client
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class UDPClient<T>(val ip: String, val port: Int, private val messageType: Class<T>) : Client<T>() {

    private var isStopped = false

    override fun connect() {
        // TODO: exception handling

        DatagramSocket().use {
            it.connect(InetAddress.getByName(ip), port)

            var connection = connections.firstOrNull() as? UDPConnection

            if (connection == null) {
                connection = UDPConnection<T>(ip + port, 1)

                openUDPConnection(connection)
            }

            val buffer = ByteArray(2048)

            while (!isStopped) {
                Arrays.fill(buffer, 0)

                val packet = DatagramPacket(buffer, buffer.size)

                it.receive(packet)

                connection.receive(packet.data)
            }

            onConnectionClosed(connection)
        }
    }

    override fun disconnect() {
        isStopped = true

        // TODO: send closing?
    }
}