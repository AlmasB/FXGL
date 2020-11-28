/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

data class ServerConfig<T>(
        val messageType: Class<T>
)

data class ClientConfig<T>(
        val messageType: Class<T>
)

data class UDPServerConfig<T>
@JvmOverloads constructor(
        val messageType: Class<T>,
        val bufferSize: Int = 2048
)

data class UDPClientConfig<T>
@JvmOverloads constructor(
        val messageType: Class<T>,
        val bufferSize: Int = 2048
)