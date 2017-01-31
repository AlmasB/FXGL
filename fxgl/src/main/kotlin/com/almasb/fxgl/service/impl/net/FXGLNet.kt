/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.service.impl.net

import com.almasb.fxgl.io.IOTask
import com.almasb.fxgl.io.taskOf
import com.almasb.fxgl.io.voidTaskOf
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.net.Client
import com.almasb.fxgl.net.DataParser
import com.almasb.fxgl.net.NetworkConnection
import com.almasb.fxgl.net.Server
import com.almasb.fxgl.service.Net
import com.google.inject.Inject
import javafx.beans.value.ChangeListener
import java.io.InputStreamReader
import java.io.Serializable
import java.net.URL
import java.nio.file.Path
import java.util.*

/**
 * Main Net service provider for FXGL.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FXGLNet
@Inject private constructor() : Net {

    override fun downloadTask(url: String): IOTask<Path> = DownloadTask(url)

    override fun openStreamTask(url: String) = taskOf("openStream($url)", { URL(url).openStream() })

    /**
     * Loads pom.xml from GitHub server's master branch
     * and parses the "version" tag.
     */
    override fun getLatestVersionTask() = openStreamTask(FXGL.getString("url.pom")).then {

        return@then taskOf("latestVersion", {

            InputStreamReader(it).useLines {
                return@taskOf it.first { it.contains("<version>") }
                        .trim()
                        .removeSurrounding("<version>", "</version>")
            }
        })
    }

    override fun openBrowserTask(url: String) = voidTaskOf("openBrowser($url)", { FXGL.getApp().hostServices.showDocument(url) })

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
        return taskOf("Create Host", {
            val server = Server()
            server.parsers = dummy.parsers

            server.connectionActiveProperty().addListener(connectionListener)

            // wait 1 minute
            server.startAndWait(60)

            connectionInternal?.connectionActiveProperty()?.removeListener(connectionListener)
            connectionInternal = server

            return@taskOf server
        })
    }

    override fun connectMultiplayerTask(serverIP: String): IOTask<Client> {
        return taskOf("Connect To Host", {
            val client = Client(serverIP)
            client.parsers = dummy.parsers

            client.connectionActiveProperty().addListener(connectionListener)

            client.connect()

            connectionInternal?.connectionActiveProperty()?.removeListener(connectionListener)
            connectionInternal = client

            return@taskOf client
        })
    }
}