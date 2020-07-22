/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net;

import java.net.Socket;
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
public abstract class Endpoint<T> {

    // TODO: observable?
    private List<Connection<T>> connections = new ArrayList<>();

    private Consumer<Connection<T>> onConnected = c -> {};
    private Consumer<Connection<T>> onDisconnected = c -> {};

    /**
     * Send given message to all active connections.
     */
    public final void broadcast(T message) {
        for (int i = 0; i < connections.size(); i++) {
            connections.get(i).send(message);
        }
    }

    public final void setOnConnected(Consumer<Connection<T>> onConnected) {
        this.onConnected = onConnected;
    }

    public final void setOnDisconnected(Consumer<Connection<T>> onDisconnected) {
        this.onDisconnected = onDisconnected;
    }

    protected final void openNewConnection(Socket socket, int connectionNum, Class<T> messageType) throws Exception {
        socket.setTcpNoDelay(true);

        var writer = Writers.INSTANCE.getWriter(messageType, socket.getOutputStream());
        var reader = Readers.INSTANCE.getReader(messageType, socket.getInputStream());

        var connection = new Connection<T>(socket, connectionNum, writer, reader);

        onConnectionOpened(connection);

        new ConnectionThread("ConnectionSendThread-" + connectionNum, () -> {
            while (connection.isConnected()) {
                connection.send();
            }
        }).start();

        new ConnectionThread("ConnectionRecvThread-" + connectionNum, () -> {
            while (connection.isConnected()) {
                connection.receive();
            }

            onConnectionClosed(connection);
        }).start();
    }

    private void onConnectionOpened(Connection<T> connection) {
        connections.add(connection);

        onConnected.accept(connection);
    }

    private void onConnectionClosed(Connection<T> connection) {
        connections.remove(connection);

        onDisconnected.accept(connection);
    }

    /**
     * @return unmodifiable list of active connections (for clients, max size is 1)
     */
    public final List<Connection<T>> getConnections() {
        return List.copyOf(connections);
    }

    private static class ConnectionThread extends Thread {

        ConnectionThread(String name, Runnable action) {
            super(action, name);
            setDaemon(true);
        }
    }
}
