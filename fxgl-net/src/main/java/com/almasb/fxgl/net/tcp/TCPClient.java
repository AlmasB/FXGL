/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net.tcp;

import com.almasb.fxgl.net.Client;
import com.almasb.fxgl.net.SocketConnection;

import java.net.Socket;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 * @author Jordan O'Hara (jordanohara96@gmail.com)
 * @author Byron Filer (byronfiler348@gmail.com)
 */
public class TCPClient extends Client {

    private String ip;
    private int port;

    public TCPClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void connect() {
        try {
            Socket socket = new Socket(ip, port);
            socket.setTcpNoDelay(true);

            var connection = new SocketConnection(socket, 1);

            onNewConnection(connection);

            new ConnectionThread(connection).start();

        } catch (Exception e) {

            // TODO:
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        getConnections().forEach(SocketConnection::terminate);
    }
}
