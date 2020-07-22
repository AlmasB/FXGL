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
public final class Connection<T> {

    private static final Logger log = Logger.get(Connection.class);

    private ReadOnlyBooleanWrapper isConnectedProperty = new ReadOnlyBooleanWrapper(true);

    private Socket socket;
    private int connectionNum;
    private MessageWriter<T> writer;
    private MessageReader<T> reader;

    private PropertyMap localSessionData = new PropertyMap();

    private List<MessageHandler<T>> messageHandlers = new ArrayList<>();
    private List<MessageHandler<T>> messageHandlersFX = new ArrayList<>();

    private BlockingQueue<T> messageQueue = new ArrayBlockingQueue<>(100);

    public Connection(Socket socket, int connectionNum, MessageWriter<T> writer, MessageReader<T> reader) {
        this.socket = socket;
        this.connectionNum = connectionNum;
        this.writer = writer;
        this.reader = reader;
    }

    public PropertyMap getLocalSessionData() {
        return localSessionData;
    }

    public Socket getSocket() {
        return socket;
    }

    public int getConnectionNum() {
        return connectionNum;
    }

    public ReadOnlyBooleanProperty connectedProperty() {
        return isConnectedProperty.getReadOnlyProperty();
    }

    public boolean isConnected() {
        return isConnectedProperty.getValue();
    }

    public void addMessageHandler(MessageHandler<T> handler) {
        messageHandlers.add(handler);
    }

    public void removeMessageHandler(MessageHandler<T> handler) {
        messageHandlers.remove(handler);
    }

    public void addMessageHandlerFX(MessageHandler<T> handler) {
        messageHandlersFX.add(handler);
    }

    public void removeMessageHandlerFX(MessageHandler<T> handler) {
        messageHandlersFX.remove(handler);
    }

    void send() {
        try {
            var message = messageQueue.take();

            writer.write(message);
        } catch (Exception e) {

            // TODO:

            e.printStackTrace();
        }
    }

    public void send(T message) {
        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {

            // TODO:

            e.printStackTrace();
        }
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    void receive() {
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

            if (!socket.isClosed()) {
                log.debug("Connection " + connectionNum + " was unexpectedly disconnected: " + e.getMessage());

                terminate();
            }

        } catch (Exception e) {
            log.warning("Connection " + connectionNum + " had unspecified error during receive()", e);

            terminate();
        }
    }

    public void terminate() {
        if (!isConnected()) {
            log.warning("Attempted to close connection " + connectionNum + " but it is already closed.");
            return;
        }

        log.debug("Closing connection " + connectionNum);

        try {
            // closing socket auto-closes in and out streams
            socket.close();

            log.debug("Connection " + connectionNum + " was correctly closed from local endpoint.");
        } catch (Exception e) {
            log.warning("Error during socket.close()", e);
        }

        isConnectedProperty.set(false);
    }
}




//    public void setMessageHandler(OLDMH<?> handler) {
//        this.handler = handler;
//
//        try {
//            handler.setConnection(this);
//            handler.onInitialize(socket.getOutputStream(), socket.getInputStream());
//        } catch (Exception e) {
//            log.warning("Failed to initialize message handler", e);
//        }
//    }