/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net.tcp;

import com.almasb.fxgl.core.collection.PropertyMap;
import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.net.Connection;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;

import java.io.EOFException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * The socket closing (incl. out and in streams) responsibility lies within this class.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 * @author Jordan O'Hara (jordanohara96@gmail.com)
 * @author Byron Filer (byronfiler348@gmail.com)
 */
public final class SocketConnection<T> extends Connection<T> {

    private Socket socket;

    public SocketConnection(Socket socket, int connectionNum) {
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