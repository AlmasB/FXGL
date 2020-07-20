/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net;

import com.almasb.fxgl.logging.Logger;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Base class for message handlers.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 * @author Jordan O'Hara (jordanohara96@gmail.com)
 * @author Byron Filer (byronfiler348@gmail.com)
 */
public abstract class MessageHandler<T> {

    protected static final Logger log = Logger.get(MessageHandler.class);

    /**
     * Called to allow the message handler to initialize in and out streams.
     *
     * @param out connection out stream
     * @param in connection in stream
     * @throws Exception if any errors
     */
    protected abstract void onInitialize(OutputStream out, InputStream in) throws Exception;

    /**
     * Blocks while sending message.
     */
    public final void send(T message) {
        try {
            write(message);
        } catch (Exception e) {
            // TODO:
            log.warning("Error during send()", e);
        }
    }

    /**
     * Blocks while writing message.
     */
    protected abstract void write(T message) throws Exception;

    /**
     * Blocks while reading message.
     */
    protected abstract T read() throws Exception;

    /**
     * Called when a message has been received from the connection.
     * This is called on a background thread.
     */
    public void onReceive(T message) {}

    /**
     * Called when a message has been received from the connection.
     * This is called on the JavaFX Application thread only if JavaFX platform has been initialized.
     */
    public void onReceiveFX(T message) {}
}
