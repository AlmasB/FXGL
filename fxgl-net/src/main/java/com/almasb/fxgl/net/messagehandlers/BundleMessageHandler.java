/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net.messagehandlers;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.MessageHandler;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Message handler that communicates using serializable Bundle objects.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 * @author Jordan O'Hara (jordanohara96@gmail.com)
 * @author Byron Filer (byronfiler348@gmail.com)
 */
public abstract class BundleMessageHandler extends MessageHandler<Bundle> {

    private ObjectOutputStream out;
    private ObjectInputStream in;

    @Override
    protected final void onInitialize(OutputStream out, InputStream in) throws Exception {
        this.out = new ObjectOutputStream(out);
        this.in = new ObjectInputStream(in);
    }

    @Override
    protected void write(Bundle message) throws Exception {
        out.writeObject(message);
    }

    @Override
    protected Bundle read() throws Exception {
        return (Bundle) in.readObject();
    }
}

