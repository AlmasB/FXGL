/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net.udp

import com.almasb.fxgl.net.Connection

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class UDPConnection<T>(
        /**
         * Remote ip.
         */
        val fullIP: String, connectionNum: Int) : Connection<T>(connectionNum) {

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

            // TODO: convert to byte array?

        } catch (e: Exception) {

            // TODO:
            e.printStackTrace()
        }
    }

    internal fun receive(data: ByteArray) {
        // TODO: this needs to call message handlers ...
    }
}