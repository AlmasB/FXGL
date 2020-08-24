/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net;

import com.almasb.fxgl.core.collection.PropertyMap;
import com.almasb.fxgl.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;

import java.io.EOFException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class Connection<T> {

    protected static final Logger log = Logger.get(Connection.class);

    private ReadOnlyBooleanWrapper isConnectedProperty = new ReadOnlyBooleanWrapper(true);

    private int connectionNum;

    private PropertyMap localSessionData = new PropertyMap();

    protected final List<MessageHandler<T>> messageHandlers = new ArrayList<>();
    protected final List<MessageHandler<T>> messageHandlersFX = new ArrayList<>();

    protected BlockingQueue<T> messageQueue = new ArrayBlockingQueue<>(100);

    public Connection(int connectionNum) {
        this.connectionNum = connectionNum;
    }

    public final PropertyMap getLocalSessionData() {
        return localSessionData;
    }

    public final int getConnectionNum() {
        return connectionNum;
    }

    public final ReadOnlyBooleanProperty connectedProperty() {
        return isConnectedProperty.getReadOnlyProperty();
    }

    public final boolean isConnected() {
        return isConnectedProperty.getValue();
    }

    public final void addMessageHandler(MessageHandler<T> handler) {
        messageHandlers.add(handler);
    }

    public final void removeMessageHandler(MessageHandler<T> handler) {
        messageHandlers.remove(handler);
    }

    public final void addMessageHandlerFX(MessageHandler<T> handler) {
        messageHandlersFX.add(handler);
    }

    public final void removeMessageHandlerFX(MessageHandler<T> handler) {
        messageHandlersFX.remove(handler);
    }

    public final void send(T message) {
        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {

            // TODO:

            e.printStackTrace();
        }
    }

    // TODO: these 2 methods below only for TCP socket?

    void send(MessageWriter<T> writer) {
        try {
            var message = messageQueue.take();

            writer.write(message);
        } catch (Exception e) {

            // TODO:

            e.printStackTrace();
        }
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    void receive(MessageReader<T> reader) {
        try {
            var message = reader.read();

            messageHandlers.forEach(h -> h.onReceive(this, message));

            try {
                Platform.runLater(() -> messageHandlersFX.forEach(h -> h.onReceive(this, message)));
            } catch (IllegalStateException e) {
                // if javafx is not initialized then ignore
            }

        } catch (EOFException e) {
            log.debug("Connection " + connectionNum + " was correctly closed from remote endpoint.");

            terminate();
        } catch (SocketException e) {

            if (!isClosedLocally()) {
                log.debug("Connection " + connectionNum + " was unexpectedly disconnected: " + e.getMessage());

                terminate();
            }

        } catch (Exception e) {
            log.warning("Connection " + connectionNum + " had unspecified error during receive()", e);

            terminate();
        }
    }

    public final void terminate() {
        if (!isConnected()) {
            log.warning("Attempted to close connection " + connectionNum + " but it is already closed.");
            return;
        }

        log.debug("Closing connection " + connectionNum);

        try {
            terminateImpl();

            log.debug("Connection " + connectionNum + " was correctly closed from local endpoint.");
        } catch (Exception e) {
            log.warning("Error during socket.close()", e);
        }

        isConnectedProperty.set(false);
    }

    protected abstract boolean isClosedLocally();

    protected abstract void terminateImpl() throws Exception;
}
