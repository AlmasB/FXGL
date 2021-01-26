/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net;

import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.net.tcp.TCPConnection;
import com.almasb.fxgl.net.udp.UDPConnection;

import java.io.EOFException;
import java.net.Socket;
import java.net.SocketException;
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

    protected final void openTCPConnection(Socket socket, int connectionNum, Class<T> messageType) throws Exception {
        log.debug(getClass().getSimpleName() + " opening new connection (" + connectionNum + ") from " + socket.getInetAddress() + ":" + socket.getPort() + " type: " + messageType);

        socket.setTcpNoDelay(true);

        Connection<T> connection = new TCPConnection<T>(socket, connectionNum);

        onConnectionOpened(connection);

        var sendThreadName = getClass().getSimpleName() + "_SendThread-" + connectionNum;
        var recvThreadName = getClass().getSimpleName() + "_RecvThread-" + connectionNum;

        new ConnectionThread(sendThreadName, () -> {

            try {
                var writer = Writers.INSTANCE.getTCPWriter(messageType, socket.getOutputStream());

                while (connection.isConnected()) {
                    var message = connection.messageQueue.take();

                    writer.write(message);
                }
            } catch (Exception e) {
                log.warning(sendThreadName + " crashed", e);
            }
        }).start();

        new ConnectionThread(recvThreadName, () -> {
            try {
                var reader = Readers.INSTANCE.getTCPReader(messageType, socket.getInputStream());

                while (connection.isConnected()) {
                    try {
                        var message = reader.read();

                        connection.notifyMessageReceived(message);

                    } catch (EOFException e) {
                        log.debug("Connection " + connectionNum + " was correctly closed from remote endpoint.");

                        connection.terminate();
                    } catch (SocketException e) {

                        if (!connection.isClosedLocally()) {
                            log.debug("Connection " + connectionNum + " was unexpectedly disconnected: " + e.getMessage());

                            connection.terminate();
                        }

                    } catch (Exception e) {
                        log.warning("Connection " + connectionNum + " had unspecified error during receive()", e);

                        connection.terminate();
                    }
                }
            } catch (Exception e) {
                log.warning(recvThreadName + " crashed", e);
            }

            onConnectionClosed(connection);
        }).start();
    }

    protected final void openUDPConnection(UDPConnection<T> connection, Class<T> messageType) {
        log.debug("Opening UDP connection (" + connection.getConnectionNum() + ")");

        onConnectionOpened(connection);

        var sendThreadName = getClass().getSimpleName() + "_SendThread-" + connection.getConnectionNum();
        var recvThreadName = getClass().getSimpleName() + "_RecvThread-" + connection.getConnectionNum();

        new ConnectionThread(sendThreadName, () -> {

            try {
                while (connection.isConnected()) {
                    var message = connection.messageQueue.take();

                    var bytes = Writers.INSTANCE.getUDPWriter(messageType).write(message);

                    connection.sendUDP(bytes);
                }
            } catch (Exception e) {
                log.warning(sendThreadName + " crashed", e);
            }
        }).start();

        new ConnectionThread(recvThreadName, () -> {

            try {
                var reader = Readers.INSTANCE.getUDPReader(messageType);

                while (connection.isConnected()) {
                    var bytes = connection.getRecvQueue().take();

                    var message = reader.read(bytes);

                    ((Connection<T>) connection).notifyMessageReceived(message);
                }
            } catch (Exception e) {
                log.warning(recvThreadName + " crashed", e);
            }
        }).start();
    }

    private void onConnectionOpened(Connection<T> connection) {
        log.debug(getClass().getSimpleName() + " successfully opened connection (" + connection.getConnectionNum() + ")");

        connections.add(connection);

        try {
            onConnected.accept(connection);
        } catch (Exception e) {
            log.warning("Exception occurred in onConnected callback", e);
        }
    }

    protected final void onConnectionClosed(Connection<T> connection) {
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
