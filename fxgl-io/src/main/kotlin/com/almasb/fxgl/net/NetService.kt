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
import javafx.application.Platform
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.property.ReadOnlyDoubleWrapper
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

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

    fun newUDPServer(port: Int): Server<Bundle> = UDPServer(port, UDPServerConfig(Bundle::class.java))
    fun <T> newUDPServer(port: Int, config: UDPServerConfig<T>): Server<T> = UDPServer(port, config)

    fun newUDPClient(ip: String, port: Int): Client<Bundle> = UDPClient(ip, port, UDPClientConfig(Bundle::class.java))
    fun <T> newUDPClient(ip: String, port: Int, config: UDPClientConfig<T>): Client<T> = UDPClient(ip, port, config)

    /**
     * @param url web url of a file, e.g. https://raw.githubusercontent.com/AlmasB/FXGL/dev/README.md
     * @param fileName the file will be saved with this name, e.g. README.md
     * @param callback download progress info
     * @return task that downloads a file from given url into running directory
     */
    fun downloadTask(url: String, fileName: String, callback: DownloadCallback): IOTask<Path> {
        return DownloadTask(url, fileName, callback)
    }

    /**
     * @param url web url of a file, e.g. https://raw.githubusercontent.com/AlmasB/FXGL/dev/README.md
     * @param fileName the file will be saved with this name, e.g. README.md
     * @return task that downloads a file from given url into running directory
     */
    fun downloadTask(url: String, fileName: String): IOTask<Path> {
        return DownloadTask(url, fileName)
    }

    /**
     * @param url web url of a file, e.g. https://raw.githubusercontent.com/AlmasB/FXGL/dev/README.md
     * @param fileName the file will be saved with this name, e.g. README.md
     * @param callback download progress info
     * @return task that downloads a file from given url into running directory
     */
    fun downloadTask(url: URL, fileName: String, callback: DownloadCallback): IOTask<Path> {
        return DownloadTask(url.toExternalForm(), fileName, callback)
    }

    /**
     * @param url web url of a file, e.g. https://raw.githubusercontent.com/AlmasB/FXGL/dev/README.md
     * @param fileName the file will be saved with this name, e.g. README.md
     * @return task that downloads a file from given url into running directory
     */
    fun downloadTask(url: URL, fileName: String): IOTask<Path> {
        return DownloadTask(url.toExternalForm(), fileName)
    }

    // we accept both url and file name as Strings to delay any parsing errors to onExecute()
    private class DownloadTask(

            /**
             * Download file URL.
             */
            private val urlString: String,

            /**
             * To save as.
             */
            private val fileName: String,

            private val callback: DownloadCallback = DownloadCallback()

    ) : IOTask<Path>("DownloadTask($urlString, $fileName)") {

        override fun onExecute(): Path {
            val url = URL(urlString)
            val file = Paths.get(fileName)

            val connection = url.openConnection()

            val downloadSize = connection.contentLengthLong

            val inStream: InputStream = connection.getInputStream()
            val outStream: OutputStream = Files.newOutputStream(file)

            outStream.use {
                // this should also close the "connection" above
                inStream.use {

                    // adapted from JDK InputStream.transferTo
                    var transferred: Long = 0
                    val buffer = ByteArray(8192)
                    var read: Int
                    while (inStream.read(buffer, 0, 8192).also { read = it } >= 0) {
                        if (isCancelled)
                            throwCancelException()

                        outStream.write(buffer, 0, read)
                        transferred += read.toLong()

                        // update progress on the JavaFX thread
                        Platform.runLater {
                            callback.progressProp.value = transferred.toDouble() / downloadSize
                        }
                    }
                }
            }

            return file
        }
    }
}

class DownloadCallback {

    internal val progressProp = ReadOnlyDoubleWrapper()

    fun progressProperty(): ReadOnlyDoubleProperty = progressProp.readOnlyProperty
}

//    /**
//     * @param url link to open
//     * @return task that opens default browser with given url
//     */
//    fun openBrowserTask(url: String) = IOTask.of("openBrowser($url)") {
//        FXGL.getApp().hostServices.showDocument(url)
//    }