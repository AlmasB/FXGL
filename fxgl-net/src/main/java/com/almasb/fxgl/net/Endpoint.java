/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net;

import com.almasb.fxgl.logging.Logger;

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

    private static final Logger log = Logger.get(Endpoint.class);

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

    /**
     * The given callback function is called when a new connection with another Endpoint has been established.
     * Message handlers should be added within the callback function.
     * It is also safe to call connection.send() or broadcast() within the callback function.
     * Such messages will arrive in correct order provided that the other Endpoint also added message handlers
     * within the callback function.
     */
    public final void setOnConnected(Consumer<Connection<T>> onConnected) {
        this.onConnected = onConnected;
    }

    public final void setOnDisconnected(Consumer<Connection<T>> onDisconnected) {
        this.onDisconnected = onDisconnected;
    }

    protected final void openNewConnection(Socket socket, int connectionNum, Class<T> messageType) throws Exception {
        log.debug(getClass().getSimpleName() + " opening new connection (" + connectionNum + ") from " + socket.getInetAddress() + ":" + socket.getPort() + " type: " + messageType);

        socket.setTcpNoDelay(true);

        var connection = new Connection<T>(socket, connectionNum);

        onConnectionOpened(connection);

        new ConnectionThread(getClass().getSimpleName() + "_SendThread-" + connectionNum, () -> {

            try {
                var writer = Writers.INSTANCE.getWriter(messageType, socket.getOutputStream());

                while (connection.isConnected()) {
                    connection.send(writer);
                }
            } catch (Exception e) {

                // TODO:
                e.printStackTrace();
            }
        }).start();

        new ConnectionThread(getClass().getSimpleName() +"_RecvThread-" + connectionNum, () -> {
            try {
                var reader = Readers.INSTANCE.getReader(messageType, socket.getInputStream());

                while (connection.isConnected()) {
                    connection.receive(reader);
                }
            } catch (Exception e) {

                // TODO:
                e.printStackTrace();
            }

            onConnectionClosed(connection);
        }).start();
    }

    private void onConnectionOpened(Connection<T> connection) {
        log.debug(getClass().getSimpleName() + " successfully opened connection (" + connection.getConnectionNum() + ")");

        connections.add(connection);

        onConnected.accept(connection);
    }

    private void onConnectionClosed(Connection<T> connection) {
        log.debug(getClass().getSimpleName() + " connection (" + connection.getConnectionNum() + ") was closed");

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
