/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net;

import com.almasb.fxgl.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;

import java.io.EOFException;
import java.net.Socket;
import java.net.SocketException;

/**
 * The socket closing (incl. out and in streams) responsibility lies within this class.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 * @author Jordan O'Hara (jordanohara96@gmail.com)
 * @author Byron Filer (byronfiler348@gmail.com)
 */
public final class SocketConnection {

    private static final Logger log = Logger.get(SocketConnection.class);

    private ReadOnlyBooleanWrapper isConnectedProperty = new ReadOnlyBooleanWrapper(true);

    private Socket socket;
    private int connectionNum;

    private MessageHandler handler;

    public SocketConnection(Socket socket, int connectionNum) {
        this.socket = socket;
        this.connectionNum = connectionNum;
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

    public void setMessageHandler(MessageHandler<?> handler) {
        this.handler = handler;

        try {
            handler.onInitialize(socket.getOutputStream(), socket.getInputStream());
        } catch (Exception e) {
            log.warning("Failed to initialize message handler", e);
        }
    }

    public void receive() {
        if (handler != null) {
            try {
                var message = handler.read();

                handler.onReceive(message);

                try {
                    Platform.runLater(() -> handler.onReceiveFX(message));
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

            log.debug("Closing " + connectionNum + " was correctly closed from local endpoint.");
        } catch (Exception e) {
            log.warning("Error during socket.close()", e);
        }

        isConnectedProperty.set(false);
    }
}
