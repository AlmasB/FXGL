/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net.tcp;

import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.net.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 * @author Jordan O'Hara (jordanohara96@gmail.com)
 * @author Byron Filer (byronfiler348@gmail.com)
 * */
public final class TCPServer<T> extends Server<T> {

    private static final Logger log = Logger.get(TCPServer.class);

    private boolean isStopped = false;

    private int port;
    private Class<T> messageType;
    private ServerSocket serverSocket;

    public TCPServer(int port, Class<T> messageType) {
        this.port = port;
        this.messageType = messageType;
    }

    @Override
    protected void start() {
        log.debug("Starting to listen at: " + port + " type: " + messageType);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            this.serverSocket = serverSocket;

            onStartedListening();

            int connectionNum = 1;

            while (!isStopped) {
                Socket socket = serverSocket.accept();

                openTCPConnection(socket, connectionNum++, messageType);
            }

        } catch (Exception e) {

            // TODO: check logic here

            if (!isStopped) {
                throw new RuntimeException("Failed to start: " + e.getMessage(), e);
            }
        }

        onStoppedListening();
    }

    @Override
    public void stop() {
        isStopped = true;

        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            log.warning("IOException when closing server socket: " + e.getMessage(), e);
        }
    }
}