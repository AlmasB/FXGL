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

    override fun isClosedLocally(): Boolean {
        return isClosed
    }

    override fun terminateImpl() {
        isClosed = true

        // TODO:
    }

    fun sendUDP() {
        try {
            val message = messageQueue.take()

            // TODO: only serializable?
            val bytes = toByteArray(message as Serializable)

            val packet = DatagramPacket(bytes, bytes.size,
                    InetAddress.getByName(remoteIp), remotePort
            )

            socket.send(packet)

        } catch (e: Exception) {

            // TODO:
            e.printStackTrace()
        }
    }

    internal fun receive(data: ByteArray) {
        try {

            ObjectInputStream(ByteArrayInputStream(data)).use {
                // TODO: what if this is not fully received

                val message = it.readObject() as T

                messageHandlers.forEach(Consumer { it.onReceive(this, message) })

                try {
                    Platform.runLater {
                        messageHandlersFX.forEach(Consumer { it.onReceive(this, message) })
                    }
                } catch (e: IllegalStateException) {
                    // if javafx is not initialized then ignore
                }
            }


        } catch (e: java.lang.Exception) {
            // TODO:
        }
    }

    private fun toByteArray(data: Serializable): ByteArray {
        val baos = ByteArrayOutputStream()
        ObjectOutputStream(baos).use { it.writeObject(data) }
        return baos.toByteArray()
    }
}