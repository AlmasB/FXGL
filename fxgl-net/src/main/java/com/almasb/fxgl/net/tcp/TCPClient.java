/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net.tcp;

import com.almasb.fxgl.net.Client;
import com.almasb.fxgl.net.Connection;

import java.net.Socket;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 * @author Jordan O'Hara (jordanohara96@gmail.com)
 * @author Byron Filer (byronfiler348@gmail.com)
 */
public class TCPClient<T> extends Client<T> {

    private String ip;
    private int port;
    private Class<T> messageType;

    public TCPClient(String ip, int port, Class<T> messageType) {
        this.ip = ip;
        this.port = port;
        this.messageType = messageType;
    }

    @Override
    public void connect() {
        try {
            Socket socket = new Socket(ip, port);

            openNewConnection(socket, 1, messageType);

        } catch (Exception e) {

            // TODO:
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        getConnections().forEach(Connection::terminate);
    }
}
