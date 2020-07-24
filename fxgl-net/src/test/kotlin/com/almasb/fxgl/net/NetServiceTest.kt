/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net

import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.logging.ConsoleOutput
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.logging.LoggerConfig
import com.almasb.fxgl.logging.LoggerLevel
import javafx.beans.property.SimpleIntegerProperty
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertTimeoutPreemptively
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.api.fail
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class NetServiceTest {

    companion object {
        private const val TEST_PORT = 60001
    }

    private lateinit var net: NetService

    @BeforeEach
    fun setUp() {
        net = NetService()
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `Open stream to URL`() {
        val numLines = net.openStreamTask("https://raw.githubusercontent.com/AlmasB/FXGL/master/README.md")
                .run()
                .bufferedReader()
                .lines()
                .count()

        assertThat(numLines, greaterThan(0L))
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `TCP Bundle message handler`() {
        var count = 0

        assertTimeoutPreemptively(Duration.ofSeconds(2)) {
            val server = net.newTCPServer(TEST_PORT, Bundle::class.java)

            server.setOnConnected {
                count++

                // run this in a separate thread so we don't block the client
                // in production this is not necessary
                Thread(Runnable {
                    val bundle = Bundle("")
                    bundle.put("data", "Hello World Test")

                    // send data
                    it.send(bundle)

                    // and wait 0.2 sec before stopping the server
                    Thread.sleep(200)

                    server.stop()
                }).start()
            }

            val client = net.newTCPClient("localhost", TEST_PORT, Bundle::class.java)

            client.setOnConnected {
                count++

                it.addMessageHandler { connection, message ->
                    val data = message.get<String>("data")

                    assertThat(data, `is`("Hello World Test"))

                    count++
                }
            }

            server.listeningProperty().addListener { _, _, isListening ->
                if (isListening) {
                    client.connectTask().run()
                }
            }

            server.startTask()
                    .onFailure { e -> fail { "Server Start failed $e" } }
                    .run()

            assertThat(count, `is`(3))
        }
    }
}