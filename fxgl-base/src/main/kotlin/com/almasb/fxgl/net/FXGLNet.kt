/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.core.concurrent.IOTask
import com.almasb.fxgl.util.Optional
import javafx.beans.value.ChangeListener
import java.io.InputStreamReader
import java.io.Serializable
import java.net.URL
import java.nio.file.Path

/**
 * Main Net service provider for FXGL.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FXGLNet : Net {

    override fun downloadTask(url: String): IOTask<Path> = DownloadTask(url)

    override fun openStreamTask(url: String) = IOTask.of("openStream($url)", { URL(url).openStream() })

    /**
     * Loads pom.xml from GitHub server's master branch
     * and parses the "version" tag.
     */
    override fun getLatestVersionTask() = openStreamTask(FXGL.getProperties().getString("url.pom")).then {

        return@then IOTask.of("latestVersion", {

            InputStreamReader(it).useLines {
                return@of it.first { it.contains("<version>") }
                        .trim()
                        .removeSurrounding("<version>", "</version>")
            }
        })
    }

    override fun openBrowserTask(url: String) = IOTask.ofVoid("openBrowser($url)", { FXGL.getApp().hostServices.showDocument(url) })

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