/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net.tcp;

import com.almasb.fxgl.net.Connection;

import java.net.Socket;

/**
 * The socket closing (incl. out and in streams) responsibility lies within this class.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 * @author Jordan O'Hara (jordanohara96@gmail.com)
 * @author Byron Filer (byronfiler348@gmail.com)
 */
public final class TCPConnection<T> extends Connection<T> {

    private Socket socket;

    public TCPConnection(Socket socket, int connectionNum) {
        super(connectionNum);
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    protected boolean isClosedLocally() {
        return socket.isClosed();
    }

    @Override
    protected void terminateImpl() throws Exception {
        // closing socket auto-closes in and out streams
        socket.close();
    }
}