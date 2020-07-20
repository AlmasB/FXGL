/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net.messagehandlers;

import com.almasb.fxgl.net.MessageHandler;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * TODO: check out / in work
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 * @author Jordan O'Hara (jordanohara96@gmail.com)
 * @author Byron Filer (byronfiler348@gmail.com)
 */
public abstract class ByteArrayMessageHandler extends MessageHandler<byte[]> {

    private OutputStream out;
    private InputStream in;

    @Override
    protected void onInitialize(OutputStream out, InputStream in) throws Exception {
        this.out = out;
        this.in = in;
    }

    @Override
    protected void write(byte[] message) throws Exception {
        out.write(message);
    }

    @Override
    protected byte[] read() throws Exception {
        return in.readAllBytes();
    }
}