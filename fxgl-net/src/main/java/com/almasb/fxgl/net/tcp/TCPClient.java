/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net.tcp;

import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.net.Client;
import com.almasb.fxgl.net.Connection;

import java.net.Socket;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 * @author Jordan O'Hara (jordanohara96@gmail.com)
 * @author Byron Filer (byronfiler348@gmail.com)
 */
public class TCPClient<T> extends Client<T> {

    private static final Logger log = Logger.get(TCPClient.class);

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
        log.debug("Connecting to " + ip + ":" + port + " type: " + messageType);

        Socket socket;

        try {
            socket = new Socket(ip, port);

            log.debug("Created socket to " + ip + ":" + port);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create a socket to address " + ip + " : " + port + " Error: " + e, e);
        }

        try {
            openTCPConnection(socket, 1, messageType);
        } catch (Exception e) {
            // in case we managed to partially open the connection
            disconnect();

            throw new RuntimeException("Failed to open TCP connection to " + ip + ":" + port + " Error: " + e, e);
        }
    }

    @Override
    public void disconnect() {
        getConnections().forEach(Connection::terminate);
    }
}
