/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.concurrent.IOTask
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.logging.Logger
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

    private val log = Logger.get(javaClass)

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
    fun <T> newTCPServer(port: Int, config: ServerConfig<T>): Server<T> = TCPServer(port, config.messageType)

    fun newTCPClient(ip: String, port: Int): Client<Bundle> = TCPClient(ip, port, Bundle::class.java)
    fun <T> newTCPClient(ip: String, port: Int,  config: ClientConfig<T>): Client<T> = TCPClient(ip, port, config.messageType)

    fun newUDPServer(port: Int): Server<Bundle> = UDPServer(port, Bundle::class.java)
    fun newUDPClient(ip: String, port: Int): Client<Bundle> = UDPClient(ip, port, Bundle::class.java)
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