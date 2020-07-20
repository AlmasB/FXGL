/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A single endpoint of a connection, i.e. client or server.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 * @author Jordan O'Hara (jordanohara96@gmail.com)
 * @author Byron Filer (byronfiler348@gmail.com)
 */
public abstract class Endpoint {

    private List<Consumer<SocketConnection>> connectionHandlers = new ArrayList<>();
    private List<SocketConnection> connections = new ArrayList<>();

    public final void addConnectionHandler(Consumer<SocketConnection> connectionHandler) {
        connectionHandlers.add(connectionHandler);
    }

    public final void removeConnectionHandler(Consumer<SocketConnection> connectionHandler) {
        connectionHandlers.remove(connectionHandler);
    }

    protected final void onNewConnection(SocketConnection connection) {
        connections.add(connection);

        connectionHandlers.forEach(handler -> handler.accept(connection));
    }

    protected final void onConnectionClosed(SocketConnection connection) {
        connections.remove(connection);
    }

    /**
     * @return unmodifiable list of active connections (for clients, max size is 1)
     */
    public final List<SocketConnection> getConnections() {
        return List.copyOf(connections);
    }
}
