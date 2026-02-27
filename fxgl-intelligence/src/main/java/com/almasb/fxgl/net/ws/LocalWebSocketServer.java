/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net.ws;

import com.almasb.fxgl.logging.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

/**
 * A simple localhost websocket server.
 * All methods are internally run in a background thread,
 * so they are safe to be called from any thread.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class LocalWebSocketServer extends WebSocketServer {

    private static final String POISON_PILL = "62jkasdy732qjkkhs63jASY_HSF";

    private final Logger log;

    private boolean hasStarted = false;

    private List<Consumer<String>> messageHandlers = new ArrayList<>();

    private SendMessageThread thread;
    private WebSocket socket = null;

    /**
     * Create a server without a name at given port.
     */
    public LocalWebSocketServer(int port) {
        this("Unnamed", port);
    }

    /**
     * Create a server with the given dev-friendly name at given port.
     */
    public LocalWebSocketServer(String serverName, int port) {
        super(new InetSocketAddress("localhost", port));

        log = Logger.get("WSServer " + serverName + ":" + port);
        thread = new SendMessageThread(serverName);
    }

    public boolean hasStarted() {
        return hasStarted;
    }

    /**
     * Add a handler for new messages.
     */
    public void addMessageHandler(Consumer<String> handler) {
        messageHandlers.add(handler);
    }

    /**
     * Remove an existing handler.
     */
    public void removeMessageHandler(Consumer<String> handler) {
        messageHandlers.remove(handler);
    }

    /**
     * @return last connected socket
     */
    public Optional<WebSocket> socket() {
        return Optional.ofNullable(socket);
    }

    /**
     * Send a String to the other end of the socket.
     */
    public void send(String data) {
        if (!isConnected()) {
            log.warning("Cannot send <" + data + "> Socket is not connected");
            return;
        }

        try {
            thread.messages.put(data);
        } catch (Exception e) {
            log.warning("Failed to send data to SendMessageThread", e);
        }
    }

    public boolean isConnected() {
        return socket != null;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        log.debug("Opened connection: " + conn.getRemoteSocketAddress());

        socket = conn;
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        log.debug("Closed connection, code: " + code);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        messageHandlers.forEach(h -> h.accept(message));
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        log.warning("WS error", ex);
    }

    @Override
    public void onStart() {
        log.debug("Server started successfully");
    }

    @Override
    public void start() {
        try {
            thread.start();
            super.start();

            hasStarted = true;
        } catch (Exception e) {
            log.warning("Failed to start WS server", e);
        }
    }

    @Override
    public void stop() {
        try {
            thread.messages.put(POISON_PILL);
            super.stop();
        } catch (Exception e) {
            log.warning("Failed to stop WS server", e);
        }
    }

    private class SendMessageThread extends Thread {
        BlockingQueue<String> messages = new ArrayBlockingQueue<>(10);

        SendMessageThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    var data = messages.take();

                    if (data.equals(POISON_PILL))
                        break;

                    socket.send(data);

                } catch (Exception e) {
                    log.warning("Failed to send data", e);
                }
            }
        }
    }
}
