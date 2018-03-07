/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net;

import com.almasb.fxgl.core.logging.Logger;
import com.almasb.fxgl.util.Consumer;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a communication between two machines over network.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public abstract class NetworkConnection {

    private static final Logger log = Logger.get(NetworkConnection.class);

    protected Map<Class<?>, DataParser<? super Serializable>> parsers = new HashMap<>();

    public Map<Class<?>, DataParser<? super Serializable>> getParsers() {
        return parsers;
    }

    public void setParsers(Map<Class<?>, DataParser<? super Serializable>> parsers) {
        this.parsers = parsers;
    }

    private ReadOnlyBooleanWrapper connectionActive = new ReadOnlyBooleanWrapper(false);

    public final ReadOnlyBooleanProperty connectionActiveProperty() {
        return connectionActive.getReadOnlyProperty();
    }

    public boolean isConnectionActive() {
        return connectionActive.get();
    }

    private Runnable onConnectionOpen = null;

    public void setOnConnectionOpen(Runnable onConnectionOpen) {
        this.onConnectionOpen = onConnectionOpen;
    }

    protected void onConnectionOpen() {
        if (onConnectionOpen != null)
            onConnectionOpen.run();

        connectionActive.set(true);
    }

    private Runnable onConnectionClosed = null;

    public void setOnConnectionClosed(Runnable onConnectionClosed) {
        this.onConnectionClosed = onConnectionClosed;
    }

    protected void onConnectionClosed() {
        if (onConnectionClosed != null)
            onConnectionClosed.run();

        connectionActive.set(false);
    }

    private Consumer<Throwable> exceptionHandler = null;

    public void setExceptionHandler(Consumer<Throwable> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    protected void handleError(Exception e) {
        if (exceptionHandler == null)
            throw new RuntimeException("Network error: " + e);
        else
            exceptionHandler.accept(e);
    }

    /**
     * Send a message (hint) that this end of connection is about
     * to close
     */
    protected void sendClosingMessage() {
        try {
            send(ConnectionMessage.CLOSING, NetworkProtocol.TCP);
        } catch (Exception e) {
            log.warning("TCP already disconnected or error: " + e.getMessage());
        }

        try {
            send(ConnectionMessage.CLOSING, NetworkProtocol.UDP);
        } catch (Exception e) {
            log.warning("UDP already disconnected or error: " + e.getMessage());
        }
    }

    /**
     * Register a parser for specified class. The parser
     * will be called back when an instance of the class
     * arrives from the other end of connection
     *
     * @param cl data structure class
     * @param parser the data parser
     */
    @SuppressWarnings("unchecked")
    public <T extends Serializable> void addParser(Class<T> cl, DataParser<T> parser) {
        parsers.put(cl, (DataParser<? super Serializable>) parser);
    }

    /**
     * Send data to the machine at the other end using UDP protocol.
     *
     * @param data the data object
     */
    public void send(Serializable data) {
        send(data, NetworkProtocol.UDP);
    }

    /**
     * Send data to the machine at the other end using specified protocol
     *
     * @param data the data object
     * @param protocol the protocol to use
     */
    public void send(Serializable data, NetworkProtocol protocol) {
        if (protocol == NetworkProtocol.TCP)
            sendTCP(data);
        else
            sendUDP(data);
    }

    protected abstract void sendUDP(Serializable data);

    protected abstract void sendTCP(Serializable data);

    public abstract void close();

    protected static byte[] toByteArray(Serializable data) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutput oo = new ObjectOutputStream(baos)) {
            oo.writeObject(data);
        }

        return baos.toByteArray();
    }
}
