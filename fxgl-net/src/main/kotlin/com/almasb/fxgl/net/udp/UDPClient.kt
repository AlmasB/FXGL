/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net.udp

import com.almasb.fxgl.net.Client

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class UDPClient<T>(val ip: String, val port: Int, private val messageType: Class<T>) : Client<T>() {

    override fun connect() {
        TODO()
    }

    override fun disconnect() {
        TODO()
    }
}