/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.concurrent.IOTask
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.input.*
import com.almasb.fxgl.net.tcp.TCPClient
import com.almasb.fxgl.net.tcp.TCPServer
import com.almasb.fxgl.net.udp.UDPClient
import com.almasb.fxgl.net.udp.UDPServer
import java.io.InputStream
import java.net.URL

/**
 * All operations that can be performed via networking.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class NetService : EngineService() {

    /**
     * Note: the caller is responsible for closing the stream.
     *
     * @param url link to which to open stream
     * @return task that provides stream access to given link
     */
    fun openStreamTask(url: String): IOTask<InputStream> = IOTask.of("openStream($url)") {
        URL(url).openStream()
    }

    fun newTCPServer(port: Int): Server<Bundle> = TCPServer(port, Bundle::class.java)

    fun <T> newTCPServer(port: Int, messageType: Class<T>): Server<T> = TCPServer(port, messageType)

    fun newTCPClient(ip: String, port: Int): Client<Bundle> = TCPClient(ip, port, Bundle::class.java)

    fun <T> newTCPClient(ip: String, port: Int,  messageType: Class<T>): Client<T> = TCPClient(ip, port, messageType)

    fun newUDPServer(port: Int): Server<Bundle> = UDPServer(port, Bundle::class.java)

    fun newUDPClient(ip: String, port: Int): Client<Bundle> = UDPClient(ip, port, Bundle::class.java)

    fun addInputReplicationSender(connection: Connection<Bundle>, input: Input) {
        input.addTriggerListener(object : TriggerListener() {
            override fun onActionBegin(trigger: Trigger) {
                val bundle = Bundle("ActionBegin")

                // TODO: refactor into read() write() as part of Trigger
                if (trigger.isKey) {
                    bundle.put("key", ((trigger) as KeyTrigger).key)
                } else {
                    bundle.put("btn", ((trigger) as MouseTrigger).button)
                }

                connection.send(bundle)
            }

            override fun onActionEnd(trigger: Trigger) {
                val bundle = Bundle("ActionEnd")

                if (trigger.isKey) {
                    bundle.put("key", ((trigger) as KeyTrigger).key)
                } else {
                    bundle.put("btn", ((trigger) as MouseTrigger).button)
                }

                connection.send(bundle)
            }
        })
    }

    fun addInputReplicationReceiver(connection: Connection<Bundle>, input: Input) {
        connection.addMessageHandlerFX { _, message ->
            when (message.name) {
                "ActionBegin", "ActionEnd" -> {
                    val isKeyTrigger = message.exists("key")

                    val trigger: Trigger = if (isKeyTrigger) {
                        KeyTrigger(message.get("key"))
                    } else {
                        MouseTrigger(message.get("btn"))
                    }

                    if (message.name == "ActionBegin") {
                        input.mockTriggerPress(trigger)
                    } else {
                        input.mockTriggerRelease(trigger)
                    }
                }
            }
        }
    }
}

enum class Protocol {
    TCP, UDP
}




/*
    /**
     * @param url web url of a file
     * @return task that downloads a file from given url into running directory
     */
    IOTask<Path> downloadTask(String url);
    /**
     * @param url link to open
     * @return task that opens default browser with given url
     */
    IOTask<?> openBrowserTask(String url);

    IOTask<Server> hostMultiplayerTask();

    IOTask<Client> connectMultiplayerTask(String serverIP);

class FXGLNet : Net {

    override fun downloadTask(url: String): IOTask<Path> = DownloadTask(url)

    override fun openBrowserTask(url: String) = IOTask.ofVoid("openBrowser($url)", { FXGL.getApp().hostServices.showDocument(url) })

    override fun hostMultiplayerTask(): IOTask<Server> {
        return IOTask.of("Create Host", {
            val server = Server()
            server.parsers = dummy.parsers

            server.connectionActiveProperty().addListener(connectionListener)

            // wait 1 minute
            server.startAndWait(60)

            connectionInternal?.connectionActiveProperty()?.removeListener(connectionListener)
            connectionInternal = server

            server
        })
    }

    override fun connectMultiplayerTask(serverIP: String): IOTask<Client> {
        return IOTask.of("Connect To Host", {
            val client = Client(serverIP)
            client.parsers = dummy.parsers

            client.connectionActiveProperty().addListener(connectionListener)

            client.connect()

            connectionInternal?.connectionActiveProperty()?.removeListener(connectionListener)
            connectionInternal = client

            client
        })
    }
}
 */