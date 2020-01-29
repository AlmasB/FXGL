/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.concurrent.IOTask
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
}



/*


public abstract class NetService extends EngineService {


    public abstract IOTask<InputStream> openStreamTask(String url);


//    /**
//     * @param url web url of a file
//     * @return task that downloads a file from given url into running directory
//     */
//    IOTask<Path> downloadTask(String url);
//


//
////    /**
////     * @param url link to open
////     * @return task that opens default browser with given url
////     */
////    IOTask<?> openBrowserTask(String url);
//
//    IOTask<Server> hostMultiplayerTask();
//
//    IOTask<Client> connectMultiplayerTask(String serverIP);
//
//    /**
//     * @return network connection if active or Optional.empty() if not
//     */
//    Optional<NetworkConnection> getConnection();
//
//    <T extends Serializable> void addDataParser(Class<T> cl, DataParser<T> parser);
}


import com.almasb.fxgl.core.concurrent.IOTask
import com.almasb.fxgl.core.util.Optional
import javafx.beans.value.ChangeListener
import java.io.Serializable
import java.net.URL
import java.nio.file.Path


class FXGLNet : Net {

    override fun downloadTask(url: String): IOTask<Path> = DownloadTask(url)





    //override fun openBrowserTask(url: String) = IOTask.ofVoid("openBrowser($url)", { FXGL.getApp().hostServices.showDocument(url) })

    private val dummy by lazy { Server() }
    private var connectionInternal: NetworkConnection? = null

    private val connectionListener = ChangeListener<Boolean> { o, was, active ->
        if (!active) {
            connectionInternal = null
        }
    }

    override fun getConnection(): Optional<NetworkConnection> {
        return Optional.ofNullable(connectionInternal)
    }


    override fun <T : Serializable> addDataParser(cl: Class<T>, parser: DataParser<T>) {
        dummy.addParser(cl, parser)
    }

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