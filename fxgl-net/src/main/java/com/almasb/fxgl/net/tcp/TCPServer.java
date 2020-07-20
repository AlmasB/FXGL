/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net.tcp;

import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.net.Server;
import com.almasb.fxgl.net.SocketConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 * @author Jordan O'Hara (jordanohara96@gmail.com)
 * @author Byron Filer (byronfiler348@gmail.com)
 * */
public final class TCPServer extends Server {

    private static final Logger log = Logger.get(TCPServer.class);

    private boolean isStopped = false;

    private int port;
    private ServerSocket serverSocket;

    public TCPServer(int port) {
        this.port = port;
    }

    @Override
    protected void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            this.serverSocket = serverSocket;

            onStartedListening();

            int connectionNum = 1;

            while (!isStopped) {
                Socket socket = serverSocket.accept();
                socket.setTcpNoDelay(true);

                var connection = new SocketConnection(socket, connectionNum);

                onNewConnection(connection);

                new ConnectionThread(connection).start();
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