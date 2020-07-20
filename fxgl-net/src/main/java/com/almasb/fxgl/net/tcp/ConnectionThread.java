/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net.tcp;

import com.almasb.fxgl.net.SocketConnection;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 * @author Jordan O'Hara (jordanohara96@gmail.com)
 * @author Byron Filer (byronfiler348@gmail.com)
 */
public class ConnectionThread extends Thread {
    private SocketConnection connection;

    public ConnectionThread(SocketConnection connection) {
        super("ConnectionThread-" + connection.getConnectionNum());
        this.connection = connection;
    }

    @Override
    public void run() {
        while (connection.isConnected()) {

            // TODO: if no handler for connection then we need to do something

            connection.receive();
        }
    }
}