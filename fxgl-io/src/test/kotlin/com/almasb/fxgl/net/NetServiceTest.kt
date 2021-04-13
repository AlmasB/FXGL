/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
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
import org.junit.jupiter.api.*
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import java.lang.RuntimeException
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class NetServiceTest {

    companion object {
        private const val TEST_PORT = 60001

        private const val LOREM_IPSUM =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
                "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."

        private val LARGE_DATA by lazy { Files.readAllBytes(Paths.get(NetServiceTest::class.java.getResource("LongText.txt").toURI())) }
    }

    private lateinit var net: NetService

    @BeforeEach
    fun setUp() {
        net = NetService()
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `Open stream to URL`() {
        val numLines = net.openStreamTask("https://raw.githubusercontent.com/AlmasB/FXGL/release/README.md")
                .run()
                .bufferedReader()
                .lines()
                .count()

        assertThat(numLines, greaterThan(0L))
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `Client connect task fails gracefully if could not connect`() {
        val client = net.newTCPClient("bla-bla", 12345)

        assertThrows<RuntimeException> {
            client.connect()
        }
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `TCP Bundle message handler`() {
        var count = 0

        assertTimeoutPreemptively(Duration.ofSeconds(2)) {
            val server = net.newTCPServer(TEST_PORT)

            server.setOnConnected {
                count++

                // run this in a separate thread so we don't block the client
                // in production this is not necessary
                Thread(Runnable {
                    val bundle = Bundle("")
                    bundle.put("data", "Hello World Test")

                    // send data
                    it.send(bundle)

                    bundle.put("data2", LARGE_DATA)

                    it.send(bundle)

                    // and wait 0.5 sec before stopping the server
                    Thread.sleep(500)

                    server.stop()
                }).start()
            }

            val client = net.newTCPClient("localhost", TEST_PORT)

            client.setOnConnected {
                count++

                it.addMessageHandler { connection, message ->

                    if (count == 2) {
                        val data = message.get<String>("data")

                        assertThat(data, `is`("Hello World Test"))

                        count++
                    } else if (count == 3) {
                        val data = message.get<ByteArray>("data2")

                        assertThat(data, `is`(LARGE_DATA))

                        count++
                    }
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

            assertThat(count, `is`(4))
        }
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `TCP ByteArray message handler`() {
        var count = 0

        assertTimeoutPreemptively(Duration.ofSeconds(2)) {
            val server = net.newTCPServer(TEST_PORT, ServerConfig(ByteArray::class.java))

            server.setOnConnected {
                count++

                // run this in a separate thread so we don't block the client
                // in production this is not necessary
                Thread(Runnable {

                    // send data
                    it.send(LOREM_IPSUM.toByteArray(Charsets.UTF_8))

                    it.send(byteArrayOf(1, 99, 2, 98))

                    it.send(LARGE_DATA)

                    // and wait 0.5 sec before stopping
                    Thread.sleep(500)

                    server.stop()
                }).start()
            }

            val client = net.newTCPClient("localhost", TEST_PORT, ClientConfig(ByteArray::class.java))

            client.setOnConnected {
                count++

                it.addMessageHandler { connection, message ->

                    if (count == 2) {

                        assertThat(String(message, Charsets.UTF_8), `is`(LOREM_IPSUM))
                        count++
                    } else if (count == 3) {

                        assertThat(message, `is`(byteArrayOf(1, 99, 2, 98)))
                        count++

                    } else if (count == 4) {

                        assertThat(message, `is`(LARGE_DATA))
                        count++
                    }
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

            assertThat(count, `is`(5))
        }
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `TCP String message handler`() {
        var count = 0

        assertTimeoutPreemptively(Duration.ofSeconds(2)) {
            val server = net.newTCPServer(TEST_PORT, ServerConfig(String::class.java))

            server.setOnConnected {
                count++

                // run this in a separate thread so we don't block the client
                // in production this is not necessary
                Thread(Runnable {

                    // send data
                    it.send(LOREM_IPSUM)

                    it.send("Hello world")

                    it.send(String(LARGE_DATA))

                    // and wait 0.5 sec before stopping
                    Thread.sleep(500)

                    server.stop()
                }).start()
            }

            val client = net.newTCPClient("localhost", TEST_PORT, ClientConfig(String::class.java))

            client.setOnConnected {
                count++

                it.addMessageHandler { _, message ->

                    if (count == 2) {

                        assertThat(message, `is`(LOREM_IPSUM))
                        count++
                    } else if (count == 3) {

                        assertThat(message, `is`("Hello world"))
                        count++

                    } else if (count == 4) {

                        assertThat(message, `is`(String(LARGE_DATA)))
                        count++
                    }
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

            assertThat(count, `is`(5))
        }
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `UDP Bundle message test`() {
        var count = 0

        assertTimeoutPreemptively(Duration.ofSeconds(6)) {
            val server = net.newUDPServer(TEST_PORT, UDPServerConfig(Bundle::class.java, 65535 / 2))

            server.setOnConnected {
                count++

                // run this in a separate thread so we don't block the client
                // in production this is not necessary
                Thread(Runnable {
                    while (count < 2) {
                        Thread.sleep(10)
                    }

                    val bundle = Bundle("")
                    bundle.put("data", "Hello World Test")

                    // send data
                    it.send(bundle)

                    val bundle2 = Bundle("")

                    bundle2.put("data2", LARGE_DATA)

                    it.send(bundle2)

                    // and wait until data is fully received before stopping the server

                    while (count < 4) {
                        Thread.sleep(10)
                    }

                    server.stop()
                }).start()
            }

            val client = net.newUDPClient("localhost", TEST_PORT, UDPClientConfig(Bundle::class.java, 65535 / 2))

            client.setOnConnected {
                it.addMessageHandler { connection, message ->
                    if (count == 2) {
                        val data = message.get<String>("data")

                        assertThat(data, `is`("Hello World Test"))

                        count++
                    } else if (count == 3) {
                        val data = message.get<ByteArray>("data2")

                        assertThat(data, `is`(LARGE_DATA))

                        count++
                    }
                }

                count++
            }

            server.listeningProperty().addListener { _, _, isListening ->
                if (isListening) {
                    // TODO: investigate why client.connectTask().run(), which is synchronous, blocks server...
                    client.connectAsync()
                }
            }

            server.startTask()
                    .onFailure { e -> fail { "Server Start failed $e" } }
                    .run()

            assertThat(count, `is`(4))
        }
    }
}